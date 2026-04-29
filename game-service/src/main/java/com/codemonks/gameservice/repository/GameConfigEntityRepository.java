package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.GameConfigEntity;
import com.codemonks.gameservice.entity.GameConfigId;
import com.codemonks.gameservice.enums.GameTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameConfigEntityRepository extends JpaRepository<GameConfigEntity, GameConfigId> {
    Optional<GameConfigEntity> findByIdTenantIdAndIdGameType(String tenantId, GameTypeEnum gameType);
}