package com.codemonks.gameservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "roles_json")
    private String rolesJson;
}