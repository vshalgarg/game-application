package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.SoundEventEntity;
import com.codemonks.gameservice.enums.GameTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoundEventEntityRepository extends JpaRepository<SoundEventEntity, Long> {

    List<SoundEventEntity> findByGameTypeAndThemeKeyAndIsActiveTrue(
            GameTypeEnum gameType,
            String themeKey
    );
}