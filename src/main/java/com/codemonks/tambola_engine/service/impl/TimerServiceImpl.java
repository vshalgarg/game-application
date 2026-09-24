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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TimerServiceImpl — LEGACY tick-poller.
 * <p>
 * >>> CHANGED (Option-C migration): Number-calling aur WIN-resume ab
 * >>> Supabase-side {@code pg_cron} job (process_due_tambola_rooms())
 * >>> handle karta hai — real-time production-path me ye poller
 * >>> BILKUL nahi chalta (default disabled).
 * <p>
 * Ye class sirf ek EMERGENCY ROLLBACK-FALLBACK ke roop me rakhi gayi
 * hai — agar Supabase pg_cron kisi wajah se fail/disable ho jaye,
 * to sirf application.yml me
 * {@code tambola.legacy-poller.enabled: true} set karke, redeploy
 * karke, isko turant activate kiya ja sakta hai — bina koi naya
 * code likhe.
 * <p>
 * NOTE: Agar kabhi ye poller activate karna pade, to Supabase-side
 * pg_cron job ko simultaneously disable/pause karna zaroori hai
 * (dono ek saath mat chalao — duplicate-processing hogi, harmless
 * hai version-check ki wajah se, lekin wasteful aur confusing-logs
 * denge).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimerServiceImpl {

    private static final long POLL_INTERVAL_MS = 1000;

    // >>> CHANGED: guard-flag — default false. application.yml me
    // >>> is property ko chhod bhi sakte ho (default hi false rahega),
    // >>> ya explicitly likh sakte ho:
    // >>>   tambola:
    // >>>     legacy-poller:
    // >>>       enabled: false
    @Value("${tambola.legacy-poller.enabled:false}")
    private boolean legacyPollerEnabled;

    private final TambolaGameStateRepository roomRepository;
    private final NumberGeneratorService numberGeneratorService;

    private ScheduledExecutorService executor;

    @PostConstruct
    public void start() {

        if (!legacyPollerEnabled) {
            // >>> CHANGED: normal production-path — pg_cron already
            // >>> ye kaam kar raha hai, isliye ye poller start hi
            // >>> nahi hota.
            log.info("[TAMBOLA_POLLER_DISABLED] pg_cron (Supabase-side) handles ticks now — legacy Java-poller not started");
            return;
        }

        executor = Executors.newSingleThreadScheduledExecutor(
                r -> {
                    Thread t = new Thread(r, "tambola-poller");
                    t.setDaemon(true);   // JVM shutdown ko block na kare
                    return t;
                });

        executor.scheduleWithFixedDelay(
                this::safeProcessDueRooms, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

        log.warn("[TAMBOLA_POLLER_STARTED] LEGACY POLLER ACTIVE (interval={}ms) — " +
                "this should only run as a pg_cron fallback! Confirm Supabase pg_cron " +
                "is disabled to avoid duplicate-processing.", POLL_INTERVAL_MS);
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