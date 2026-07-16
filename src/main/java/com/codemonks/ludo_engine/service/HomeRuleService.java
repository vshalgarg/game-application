package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.HomeRuleResultDTO;

public interface HomeRuleService {

    HomeRuleResultDTO processHomeRule(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId
    );
}