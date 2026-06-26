package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.response.EngineGameStateResponseDTO;

public interface TicTacToeEngine {

    EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request);
    EngineGameStateResponseDTO makeMove(EngineMoveRequestDTO request);
}
