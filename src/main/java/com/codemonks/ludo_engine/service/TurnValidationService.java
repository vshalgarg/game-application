package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;

public interface TurnValidationService {

    void validateTurn(GameStateDTO gameState, Long playerId);

}
