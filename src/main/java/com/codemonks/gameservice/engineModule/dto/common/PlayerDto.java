package com.codemonks.gameservice.engineModule.dto.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDto {
    private Long userId;
    private Integer turnOrder;
    private String side;
}
