package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomEntityRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByRoomCode(String roomCode);
}