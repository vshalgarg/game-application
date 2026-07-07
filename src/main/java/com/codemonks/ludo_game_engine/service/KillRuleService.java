package com.codemonks.ludo_game_engine.service;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.common.KillRuleResultDTO;

public interface KillRuleService {

    KillRuleResultDTO processKillRule(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId
    );
}