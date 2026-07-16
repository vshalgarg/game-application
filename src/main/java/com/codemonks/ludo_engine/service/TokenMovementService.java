package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;

public interface TokenMovementService {

    GameStateDTO moveToken(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId,
            Integer consumedDice
    );
}