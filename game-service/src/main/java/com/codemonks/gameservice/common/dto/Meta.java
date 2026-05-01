package com.codemonks.gameservice.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class Meta {

    private LocalDateTime timestamp;
    public static Meta now() {
        return Meta.builder()
                .timestamp(LocalDateTime.now())
                .build();
    }
}
