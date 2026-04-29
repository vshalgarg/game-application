package com.codemonks.gameservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "player_results",
        indexes = {
                @Index(name = "idx_player_game", columnList = "game_id"),
                @Index(name = "idx_player_user", columnList = "user_id"),
                @Index(name = "idx_player_tenant", columnList = "tenant_id"),
                @Index(name = "idx_game_rank", columnList = "game_id,rank")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_game_user", columnNames = {"game_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class PlayerResultEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "rank")
    private Integer rank;
}