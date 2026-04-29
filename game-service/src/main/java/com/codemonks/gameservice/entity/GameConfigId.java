package com.codemonks.gameservice.entity;

import com.codemonks.gameservice.enums.GameTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameConfigId implements Serializable {

    @Column(name = "tenant_id")
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type")
    private GameTypeEnum gameType;
}