package com.codemonks.gameservice.entity;

import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.enums.converter.GameTypeConverter;
import jakarta.persistence.*;
import lombok.*;


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
public class RoomEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "room_code", length = 10, nullable = false, unique = true)
    private String roomCode;

    @Convert(converter = GameTypeConverter.class)
    @Column(name = "game_type", columnDefinition = "TINYINT")
    private GameTypeEnum gameType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private RoomStatusEnum status;

}
