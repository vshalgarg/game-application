package com.codemonks.gameservice.entity;

import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.converter.GameTypeConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "sound_events",
        indexes = {
                @Index(name = "idx_sound_event_game_type", columnList = "game_type"),
                @Index(name = "idx_sound_event_key", columnList = "event_key")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sound_event_game_theme_key",
                        columnNames = {"game_type", "theme_key", "event_key"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class SoundEventEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Convert(converter = GameTypeConverter.class)
    @Column(name = "game_type", nullable = false)
    private GameTypeEnum gameType;

    @Column(name = "theme_key", nullable = false, length = 50)
    @Builder.Default
    private String themeKey = "DEFAULT";

    @Column(name = "event_key", nullable = false, length = 50)
    private String eventKey;

    @Column(name = "s3_object_key", nullable = false, length = 255)
    private String s3ObjectKey;

    @Column(name = "format", nullable = false, length = 10)
    @Builder.Default
    private String format = "mp3";

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 1L;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}