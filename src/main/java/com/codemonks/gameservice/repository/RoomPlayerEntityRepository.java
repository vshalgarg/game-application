package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.RoomPlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomPlayerEntityRepository extends JpaRepository<RoomPlayerEntity, Long> {
    boolean existsByRoomCodeAndUserId(String roomCode, Long userId);
    int countByRoomCode(String roomCode);
    Optional<RoomPlayerEntity> findByRoomCodeAndUserId(String roomCode, Long userId);
    List<RoomPlayerEntity> findByRoomCode(String roomCode);
}