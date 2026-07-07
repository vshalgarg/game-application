package com.codemonks.ludo_game_engine.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeRuleResultDTO {
    private GameStateDTO gameState;
    private boolean reachedHome;
    private Long playerId;
    private Long tokenId;
}