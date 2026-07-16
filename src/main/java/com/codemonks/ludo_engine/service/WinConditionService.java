package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.response.EngineGameStateResponseDTO;

public interface WinConditionService {

    EngineGameStateResponseDTO checkWinner(GameStateDTO gameState, Long playerId);


}
