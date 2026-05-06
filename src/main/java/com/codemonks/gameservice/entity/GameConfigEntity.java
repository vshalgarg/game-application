package com.codemonks.gameservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "game_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameConfigEntity {

    @EmbeddedId
    private GameConfigId id;

    @Column(name = "min_players", nullable = false)
    private Integer minPlayers;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    // Flexible roles config (JSON)
    @Column(name = "roles_json", columnDefinition = "TEXT")
    private String rolesJson;
}