package com.codemonks.gameservice.engineModule.dto.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDTO {
    private Long userId;
    private String displayName;
    private Integer turnOrder;
    private String side;
    private Boolean isBot;
}
