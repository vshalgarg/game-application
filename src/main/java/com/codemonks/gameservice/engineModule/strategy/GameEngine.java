package com.codemonks.gameservice.engineModule.strategy;

import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDto;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.enums.GameTypeEnum;

public interface GameEngine {

    GameTypeEnum supports();
    EngineGameStateResponseDTO startGame(EngineStartGameRequestDto request);
    EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request);
}
