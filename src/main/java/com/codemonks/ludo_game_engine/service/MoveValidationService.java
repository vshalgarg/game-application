package com.codemonks.ludo_game_engine.service;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.request.EngineMoveRequestDTO;



public interface MoveValidationService {

    void validateMove(
            GameStateDTO gameState,
            Long userId,
            Long tokenId,
            Integer consumedDice
    );
}