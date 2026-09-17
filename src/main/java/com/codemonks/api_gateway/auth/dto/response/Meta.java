package com.codemonks.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    private LocalDateTime timestamp;

    public static Meta now() {
        return Meta.builder()
                .timestamp(LocalDateTime.now())
                .build();
    }
}