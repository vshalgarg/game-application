package com.codemonks.gameservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SoundListResponseDTO {

    private String gameType;
    private String theme;
    private Map<String, SoundEventResponseDTO> events;

    @Data
    @Builder
    public static class SoundEventResponseDTO {
        private String url;
        private Long version;
    }
}