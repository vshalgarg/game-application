package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.exception.BoardExhaustedException;
import com.codemonks.tambola_engine.repository.TambolaGameStateRepository;
import com.codemonks.tambola_engine.service.NumberGeneratorService;
import com.codemonks.tambola_engine.util.OptimisticRetry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimerServiceImpl {

    private static final long POLL_INTERVAL_MS = 1000;

    private final TambolaGameStateRepository roomRepository;
    private final NumberGeneratorService numberGeneratorService;

    private ScheduledExecutorService executor;
    @PostConstruct
    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor(
                r -> {
                    Thread t = new Thread(r, "tambola-poller");
                    t.setDaemon(true);   // JVM shutdown ko block na kare
                    return t;
                });

        executor.scheduleWithFixedDelay(
                this::safeProcessDueRooms, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

        log.info("[TAMBOLA_POLLER_STARTED] interval={}ms", POLL_INTERVAL_MS);
    }

    @PreDestroy
    public void stop() {
        if (executor != null) {
            executor.shutdown();
        }
    }
    private void safeProcessDueRooms() {
        try {
            processDueRooms();
        } catch (Exception e) {
            log.error("Tambola poller tick failed - will retry next cycle", e);
        }
    }

    private void processDueRooms() {
        List<TambolaGameState> dueRooms = roomRepository.findRoomsDueForTick(Instant.now());

        if (dueRooms.isEmpty()) {
            log.debug("[TAMBOLA_TICK_SKIP] no due rooms");
            return;
        }

        log.info("[TAMBOLA_TICK] dueRooms={}", dueRooms.size());

        for (TambolaGameState room : dueRooms) {
            try {
                if (room.getStatus() == GameStatusEnum.WIN) {
                    resumeFromWin(room.getRoomId());
                } else if (room.getStatus() == GameStatusEnum.RUNNING) {
                    callNextNumber(room.getRoomId());
                }
            } catch (Exception e) {
                log.error("Failed to process tick for room {}", room.getRoomId(), e);
            }
        }
    }

    private void callNextNumber(Long roomId) {
        OptimisticRetry.attempt(() -> {
            TambolaGameState fresh = roomRepository.findById(roomId);

            if (fresh.getStatus() != GameStatusEnum.RUNNING) {
                return true;
            }

            try {
                Set<Integer> calledNumbersSet = new HashSet<>(fresh.getCalledNumbers());
                Integer nextNumber = numberGeneratorService.generateNextNumber(calledNumbersSet);

                List<Integer> updatedNumbers = new ArrayList<>(fresh.getCalledNumbers());
                updatedNumbers.add(nextNumber);

                Map<String, Object> changes = new HashMap<>();
                changes.put("called_numbers", updatedNumbers);
                changes.put("timer_interval_seconds", fresh.getTimerIntervalSeconds());
                changes.put("next_tick_at",
                        Instant.now().plusSeconds(fresh.getTimerIntervalSeconds()).toString());

                boolean updated = roomRepository.updateIfVersionMatches(
                        roomId, changes, fresh.getVersion());

                if (updated) {
                    log.info("[TAMBOLA_NUMBER_CALLED] Room:{} Number:{} TotalCalled:{} NextTickAt:{}",
                            roomId, nextNumber, updatedNumbers.size(),
                            changes.get("next_tick_at"));
                }
                return updated;

            } catch (BoardExhaustedException e) {
                log.info("Board exhausted for room {}, marking FINISHED", roomId);
                return roomRepository.updateIfVersionMatches(
                        roomId, Map.of("game_status", GameStatusEnum.FINISHED.name()), fresh.getVersion());
            }
        });
    }

    private void resumeFromWin(Long roomId) {
        OptimisticRetry.attempt(() -> {
            TambolaGameState fresh = roomRepository.findById(roomId);

            if (fresh.getStatus() != GameStatusEnum.WIN) {
                return true;
            }

            Map<String, Object> changes = new HashMap<>();
            changes.put("game_status", GameStatusEnum.RUNNING.name());
            changes.put("called_numbers", fresh.getCalledNumbers());
            changes.put("timer_interval_seconds", fresh.getTimerIntervalSeconds());
            changes.put("next_tick_at",
                    Instant.now().plusSeconds(fresh.getTimerIntervalSeconds()).toString());

            boolean updated = roomRepository.updateIfVersionMatches(roomId, changes, fresh.getVersion());

            if (updated) {
                log.info("[TAMBOLA_GAME_RESUMED] Room:{} TotalCalled:{} Status:{}",
                        roomId, fresh.getCalledNumbers().size(), GameStatusEnum.RUNNING.name());
            }
            return updated;
        });
    }
}