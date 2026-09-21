package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomEntityRepository extends JpaRepository<RoomEntity, Long> {

    Optional<RoomEntity> findByRoomCode(String roomCode);
    Optional<RoomEntity> findByRoomCodeAndTenantId(String roomCode, String tenantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RoomEntity r where r.roomCode = :roomCode")
    Optional<RoomEntity> findByRoomCodeForUpdate(@Param("roomCode") String roomCode);

    List<RoomEntity> findByStatusAndGameTypeAndCreatedAtBefore(
            RoomStatusEnum status,
            GameTypeEnum gameType,
            LocalDateTime cutoff);
}