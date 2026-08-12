package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.request.DiceRollRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.ludo_engine.dto.response.DiceRollResponseDTO;
import com.codemonks.ludo_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.ludo_engine.service.BotTurnService;
import com.codemonks.ludo_engine.service.EngineService;
import com.codemonks.ludo_engine.service.GameFlowService;
import com.codemonks.ludo_engine.service.TurnDelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameFlowServiceImpl implements GameFlowService {

    private final EngineService engineService;
    private final TurnDelayService turnDelayService;
    private final BotTurnService botTurnService;


    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request) {
        EngineGameStateResponseDTO response = engineService.startGame(request);
        botTurnService.triggerBotIfNeeded(request.getRoomId(), request.getRoomCode());
        return response;
    }

    @Override
    public EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request) {
        EngineGameStateResponseDTO response = engineService.processMove(request);
        botTurnService.triggerBotIfNeeded(request.getRoomId(), request.getRoomCode());
        return response;
    }


    @Override
    public DiceRollResponseDTO rollDice(
            DiceRollRequestDTO request
    ) {

        DiceRollResponseDTO response =
                engineService.rollDice(request);

        if (response.isDelayedTurnRotationRequired()) {

            turnDelayService.scheduleTurnContinuation(
                    request.getRoomId(),
                    null,
                    request.getPlayerId()
            );

        } else {

            botTurnService.triggerBotIfNeeded(
                    request.getRoomId(),
                    null
            );
        }

        return response;
    }
}