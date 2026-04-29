package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.PlayerResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerResultEntityRepository extends JpaRepository<PlayerResultEntity, Long> {
}