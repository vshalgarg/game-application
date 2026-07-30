package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.KillRuleResultDTO;

public interface KillRuleService {

    KillRuleResultDTO processKillRule(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId
    );
}