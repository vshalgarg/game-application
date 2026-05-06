package com.codemonks.gameservice.engineModule.strategy;

import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineResponseDTO;

public interface GameEngineStrategy {

    EngineResponseDTO startGame(EngineStartRequestDTO request);
    EngineResponseDTO processMove(EngineMoveRequestDTO request);
}
