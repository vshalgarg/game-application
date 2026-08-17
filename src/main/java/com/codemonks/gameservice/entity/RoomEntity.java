package com.codemonks.gameservice.entity;

import com.codemonks.gameservice.engineModule.enums.BotDifficultyEnum;
import com.codemonks.gameservice.engineModule.enums.MatchTypeEnum;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.enums.converter.GameTypeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "rooms",
        indexes = {
                @Index(name = "idx_room_tenant", columnList = "tenant_id"),
                @Index(name = "idx_room_status", columnList = "status"),
                @Index(name = "idx_room_code", columnList = "room_code")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_room_code", columnNames = "room_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class RoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "room_code", nullable = false, unique = true, length = 10)
    private String roomCode;

    @Convert(converter = GameTypeConverter.class)
    @Column(name = "game_type", nullable = false)
    private GameTypeEnum gameType;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = true, length = 20)
    private MatchTypeEnum matchType;

    @Enumerated(EnumType.STRING)
    @Column(name = "bot_difficulty", length = 20)
    private BotDifficultyEnum botDifficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RoomStatusEnum status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

}