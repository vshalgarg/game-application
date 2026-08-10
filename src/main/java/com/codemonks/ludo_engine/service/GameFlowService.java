package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.request.DiceRollRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.ludo_engine.dto.response.DiceRollResponseDTO;
import com.codemonks.ludo_engine.dto.response.EngineGameStateResponseDTO;

public interface GameFlowService {

    EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request);

    EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request);

    DiceRollResponseDTO rollDice(DiceRollRequestDTO request);
}