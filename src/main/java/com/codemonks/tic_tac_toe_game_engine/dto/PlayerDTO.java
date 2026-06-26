package com.codemonks.tic_tac_toe_game_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
