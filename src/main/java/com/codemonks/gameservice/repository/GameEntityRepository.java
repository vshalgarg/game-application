package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameEntityRepository extends JpaRepository<GameEntity, Long> {
    Optional<GameEntity> findByRoomCode(String roomCode);
}