package com.codemonks.ludo_engine.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    private Long playerId;
    private Integer colorIndex;
    private List<TokenDTO> tokens;
    private List<Integer> pendingDice;
    private Boolean bot;
    private String botDifficulty;
    private Boolean pendingExtraTurn;
}
