package com.codemonks.gameservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "game_results",
        indexes = {
                @Index(name = "idx_result_game", columnList = "room_id"),
                @Index(name = "idx_result_tenant", columnList = "tenant_id"),
                @Index(name = "idx_result_winner", columnList = "winner_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class GameResultEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    // winner (userId)
    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}