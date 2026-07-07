package com.codemonks.ludo_game_engine.service;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;

public interface TokenMovementService {

    GameStateDTO moveToken(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId,
            Integer consumedDice
    );
}