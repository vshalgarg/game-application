package com.codemonks.gameservice.engineModule.strategy.impl;

import com.codemonks.gameservice.client.TicTacToeFeignClient;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.enums.GameTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicTacToeEngine implements GameEngine {

    private final TicTacToeFeignClient feignClient;

    @Override
    public GameTypeEnum supports() {
        return GameTypeEnum.TIC_TAC_TOE;
    }

    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request) {
        return feignClient.start(request);
    }

    @Override
    public EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request) {
        return feignClient.move(request);
    }
}
