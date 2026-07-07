package com.codemonks.ludo_game_engine.service;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.common.HomeRuleResultDTO;

public interface HomeRuleService {

    HomeRuleResultDTO processHomeRule(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId
    );
}