package com.codemonks.gameservice.service;


import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;

public interface GameService {

    EngineGameStateResponseDTO startGame(String roomCode);
    EngineGameStateResponseDTO makeMove(String roomCode, MakeMoveRequestDTO request);
}
