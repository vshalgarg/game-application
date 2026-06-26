package com.codemonks.gameservice.engineModule.strategy.impl;

import com.codemonks.gameservice.client.LudoFeignClient;
import com.codemonks.gameservice.engineModule.dto.request.DiceRollRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.DiceRollResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.enums.GameTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LudoEngineImpl implements GameEngine {

    private final LudoFeignClient feignClient;


    @Override
    public GameTypeEnum supports() {return GameTypeEnum.LUDO;}


    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request) {
        return feignClient.start(request);
    }

    @Override
    public EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request) {
        return feignClient.move(request);
    }

    @Override
    public DiceRollResponseDTO rollDice(DiceRollRequestDTO request) {
        return feignClient.rollDice(request);
    }

}
