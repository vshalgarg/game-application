package com.codemonks.gameservice.engineModule.dto.common;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGameStateDto {
    private Long userId;
    private String symbol;
}
