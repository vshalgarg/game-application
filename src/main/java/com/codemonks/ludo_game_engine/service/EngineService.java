package com.codemonks.ludo_game_engine.service;

import com.codemonks.ludo_game_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.ludo_game_engine.dto.request.DiceRollRequestDTO;
import com.codemonks.ludo_game_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.ludo_game_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.ludo_game_engine.dto.response.DiceRollResponseDTO;
import com.codemonks.ludo_game_engine.dto.response.EngineGameStateResponseDTO;

public interface EngineService {

    EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO requestDTO);
    void publishLobbyState(RealtimeLobbyDTO lobbyDTO);
    EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request);
    DiceRollResponseDTO rollDice(DiceRollRequestDTO request);


}
