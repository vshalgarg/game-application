package com.codemonks.gameservice.entity;

import com.codemonks.gameservice.enums.GameStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "games",
        indexes = {
                @Index(name = "idx_game_room", columnList = "room_Code"),
                @Index(name = "idx_game_tenant", columnList = "tenant_id"),
                @Index(name = "idx_game_status", columnList = "status"),
                @Index(name = "idx_game_turn", columnList = "current_turn"),
                @Index(name = "idx_tenant_room", columnList = "tenant_id,room_Code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class GameEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    // Link to Room
    @Column(name = "room_Code", nullable = false)
    private String roomCode;

    // IN_PROGRESS, COMPLETED
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatusEnum status;

    // Whose turn (userId or playerId)
    @Column(name = "current_turn")
    private Long currentTurn;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @PrePersist
    public void prePersist() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }
}