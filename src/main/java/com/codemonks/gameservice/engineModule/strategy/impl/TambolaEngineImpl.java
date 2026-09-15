package com.codemonks.gameservice.engineModule.strategy.impl;

import com.codemonks.gameservice.client.TambolaFeignClient;
import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaClaimRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.TambolaClaimResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.TambolaGameSetupResponseDTO;
import com.codemonks.gameservice.engineModule.enums.GameStatusEnum;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.exceptions.GameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TambolaEngineImpl implements GameEngine {

    private final TambolaFeignClient feignClient;

    @Override
    public GameTypeEnum supports() {
        return GameTypeEnum.TAMBOLA;
    }

    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request) {
        log.info("[TAMBOLA_ENGINE] Starting game for room: {}", request.getRoomId());

         TambolaGameSetupResponseDTO engineResponse = feignClient.start(request);

        Map<String, Object> gameState = new HashMap<>();
        gameState.put("totalTicketsGenerated", engineResponse.getTotalTicketsGenerated());

        return EngineGameStateResponseDTO.builder()
                .gameState(gameState)
                .status(GameStatusEnum.valueOf(engineResponse.getStatus()))
                .build();
    }

    @Override
    public EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request) {
        log.info("[TAMBOLA_ENGINE] Processing claim for room: {}", request.getRoomId());

        Map<String, Object> moveData = request.getMoveData();
        if (moveData == null || !moveData.containsKey("ticketId") || !moveData.containsKey("ruleType")) {
            throw new GameException(ResponseErrorCodes.INVALID_REQUEST);
        }

        // NAYA — Tambola-engine ke apne TambolaClaimRequestDTO shape me convert karo.
        TambolaClaimRequestDTO claimRequest = new TambolaClaimRequestDTO(
                request.getRoomId(),
                request.getUserId(),
                Long.valueOf(moveData.get("ticketId").toString()),
                moveData.get("ruleType").toString()
        );

        TambolaClaimResponseDTO claimResponse = feignClient.submitClaim(claimRequest);

        // Same pattern jo startGame() me use kiya - Tambola ka specific
        // response generic gameState-Map ke andar wrap hota hai.
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("claimId", claimResponse.getClaimId());
        gameState.put("ruleType", claimResponse.getRuleType());
        gameState.put("claimStatus", claimResponse.getStatus());
        gameState.put("winnersCount", claimResponse.getWinnersCount());
        gameState.put("maxWinners", claimResponse.getMaxWinners());

        return EngineGameStateResponseDTO.builder()
                .gameState(gameState)
                .status(GameStatusEnum.valueOf(claimResponse.getGameStatus()))
                .build();
    }


}