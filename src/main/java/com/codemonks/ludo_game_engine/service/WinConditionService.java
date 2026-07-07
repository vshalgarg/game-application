package com.codemonks.ludo_game_engine.service;


import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.ludo_game_engine.dto.response.EngineGameStateResponseDTO;

public interface WinConditionService {

    EngineGameStateResponseDTO checkWinner(GameStateDTO gameState, Long playerId);


}
