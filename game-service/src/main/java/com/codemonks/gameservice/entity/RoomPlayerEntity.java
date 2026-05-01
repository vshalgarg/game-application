package com.codemonks.gameservice.entity;

import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomPlayerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "room_players",
        indexes = {
                @Index(name = "idx_room_player_room", columnList = "room_id"),
                @Index(name = "idx_room_player_user", columnList = "user_id"),
                @Index(name = "idx_room_player_status", columnList = "status"),
                @Index(name = "idx_room_player_tenant", columnList = "tenant_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_room_user", columnNames = {"room_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class RoomPlayerEntity extends BaseEntity{

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "user_id", length = 50, nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50, nullable = false)
    private RoomPlayerRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private RoomPlayerStatus status;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
    }
}
