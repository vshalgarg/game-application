package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.GameResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultEntityRepository extends JpaRepository<GameResultEntity, Long> {
}