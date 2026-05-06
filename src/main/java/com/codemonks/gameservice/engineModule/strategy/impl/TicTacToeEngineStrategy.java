package com.codemonks.gameservice.engineModule.strategy.impl;

import com.codemonks.gameservice.client.TicTacToeFeignClient;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineResponseDTO;
import com.codemonks.gameservice.engineModule.strategy.GameEngineStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicTacToeEngineStrategy implements GameEngineStrategy {

    private final TicTacToeFeignClient feignClient;

    @Override
    public EngineResponseDTO startGame(EngineStartRequestDTO request) {
        return feignClient.start(request);
    }

    @Override
    public EngineResponseDTO processMove(EngineMoveRequestDTO request) {
        return feignClient.move(request);
    }

}
