package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;

public interface MoveValidationService {

    void validateMove(
            GameStateDTO gameState,
            Long userId,
            Long tokenId,
            Integer consumedDice
    );
}