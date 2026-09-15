package com.codemonks.gameservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SoundEventResponseDTO {
    private Long id;
    private String gameType;
    private String themeKey;
    private String eventKey;
    private String url;
    private String format;
    private Long version;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}