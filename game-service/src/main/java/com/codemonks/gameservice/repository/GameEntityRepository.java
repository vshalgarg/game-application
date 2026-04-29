package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameEntityRepository extends JpaRepository<GameEntity, Long> {
}