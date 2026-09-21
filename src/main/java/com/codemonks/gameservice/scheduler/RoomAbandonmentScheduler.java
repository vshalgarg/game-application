package com.codemonks.gameservice.scheduler;

import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class RoomAbandonmentScheduler {

    private final RoomEntityRepository roomRepository;

    /**
     * How long a room can sit in WAITING before it's considered abandoned.
     * Configurable via application.yml: game-service.room.abandon-after-minutes
     */
    @Value("${game-service.room.abandon-after-minutes:30}")
    private long abandonAfterMinutes;

    /**
     * How often this job runs. Configurable via application.yml:
     * game-service.room.abandonment-check-interval-ms
     */
    @Scheduled(fixedDelayString = "${game-service.room.abandonment-check-interval-ms:300000}") // default: every 5 min
    @Transactional
    public void markStaleWaitingRoomsCancelled() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(abandonAfterMinutes);

        List<RoomEntity> staleRooms = roomRepository.findByStatusAndGameTypeAndCreatedAtBefore(
                RoomStatusEnum.WAITING, GameTypeEnum.TAMBOLA, cutoff);

        if (staleRooms.isEmpty()) {
            log.debug("[ROOM_ABANDONMENT_CHECK] no stale WAITING Tambola rooms found (cutoff={})", cutoff);
            return;
        }

        for (RoomEntity room : staleRooms) {
            room.setStatus(RoomStatusEnum.CANCELLED);
            room.setEndedAt(LocalDateTime.now());
            roomRepository.save(room);

            log.info("[ROOM_ABANDONED] roomId={} roomCode={} gameType={} createdAt={} - never started, marked CANCELLED",
                    room.getId(), room.getRoomCode(), room.getGameType(), room.getCreatedAt());

        }
        log.info("[ROOM_ABANDONMENT_CHECK_COMPLETE] markedCount={}", staleRooms.size());
    }
}