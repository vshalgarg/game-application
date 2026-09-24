package com.codemonks.gameservice.engineModule.strategy.impl;

import com.codemonks.gameservice.client.TambolaFeignClient;
import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaClaimRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaReplaceRulesRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaRuleConfigRequestDTO;
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
import java.util.List;
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
    public EngineGameStateResponseDTO startGame(
            EngineStartGameRequestDTO request) {

        log.info("[TAMBOLA_ENGINE_START] Room:{} RoomCode:{} Players:{} MatchType:{} TimerInterval:{}s",
                request.getRoomId(), request.getRoomCode(),
                request.getPlayers() != null ? request.getPlayers().size() : 0,
                request.getMatchType(), request.getTimerIntervalSeconds());

        TambolaGameSetupResponseDTO engineResponse =
                feignClient.start(request);

        log.info("[TAMBOLA_ENGINE_START_RESPONSE] Room:{} Status:{} TotalTickets:{}",
                request.getRoomId(), engineResponse.getStatus(),
                engineResponse.getTotalTicketsGenerated());

        Map<String, Object> gameState = new HashMap<>();
        gameState.put(
                "totalTicketsGenerated",
                engineResponse.getTotalTicketsGenerated()
        );
        // >>> CHANGED: Tambola-players (ticketCount, ticketIds, hasWon,
        // isBot) gameState-map ke andar daal rahe hain — EngineGameStateResponseDTO.players
        // field ludo/tictactoe-shaped hai (turnOrder/side/consecutiveSixCount),
        // Tambola ke liye fit nahi baithta, isliye usko null-hi-rehne-diya
        // (jaisa currentTurnUserId/winnerUserId bhi Tambola ke liye null
        // rehte hain — koi bug nahi, ye fields Tambola-me-apply-hi-nahi-hote).
        gameState.put(
                "players",
                engineResponse.getPlayers()
        );

        return EngineGameStateResponseDTO.builder()
                .gameState(gameState)
                .status(GameStatusEnum.valueOf(
                        engineResponse.getStatus()))
                .build();
    }

    @Override
    public EngineGameStateResponseDTO processMove(
            EngineMoveRequestDTO request) {

        log.info("[TAMBOLA_ENGINE] Processing claim for room: {}",
                request.getRoomId());

        Map<String, Object> moveData = request.getMoveData();

        if (moveData == null
                || !moveData.containsKey("ticketId")
                || !moveData.containsKey("ruleType")) {

            throw new GameException(
                    ResponseErrorCodes.INVALID_REQUEST
            );
        }

        log.info("[TAMBOLA_ENGINE_CLAIM_REQUEST] Room:{} Player:{} Ticket:{} Rule:{}",
                request.getRoomId(), request.getUserId(),
                moveData.get("ticketId"), moveData.get("ruleType"));

        TambolaClaimRequestDTO claimRequest =
                new TambolaClaimRequestDTO(
                        request.getRoomId(),
                        request.getUserId(),
                        Long.valueOf(
                                moveData.get("ticketId").toString()
                        ),
                        moveData.get("ruleType").toString()
                );

        TambolaClaimResponseDTO claimResponse =
                feignClient.submitClaim(claimRequest);

        log.info("[TAMBOLA_ENGINE_CLAIM_RESPONSE] Room:{} Player:{} ClaimId:{} Rule:{} ClaimStatus:{} Winners:{}/{} GameStatus:{}",
                request.getRoomId(), request.getUserId(),
                claimResponse.getClaimId(), claimResponse.getRuleType(),
                claimResponse.getStatus(), claimResponse.getWinnersCount(),
                claimResponse.getMaxWinners(), claimResponse.getGameStatus());

        Map<String, Object> gameState = new HashMap<>();

        gameState.put(
                "claimId",
                claimResponse.getClaimId()
        );

        gameState.put(
                "ruleType",
                claimResponse.getRuleType()
        );

        gameState.put(
                "claimStatus",
                claimResponse.getStatus()
        );

        gameState.put(
                "winnersCount",
                claimResponse.getWinnersCount()
        );

        gameState.put(
                "maxWinners",
                claimResponse.getMaxWinners()
        );

        return EngineGameStateResponseDTO.builder()
                .gameState(gameState)
                .status(GameStatusEnum.valueOf(
                        claimResponse.getGameStatus()
                ))
                .build();
    }

    @Override
    public void publishLobbyState(
            RealtimeLobbyDTO lobbyDTO) {

        log.info("[TAMBOLA_ENGINE_LOBBY] Room:{} RoomCode:{} Status:{} Players:{}",
                lobbyDTO.getRoomId(), lobbyDTO.getRoomCode(),
                lobbyDTO.getRoomStatus(),
                lobbyDTO.getPlayers() != null ? lobbyDTO.getPlayers().size() : 0);

        feignClient.publishLobby(lobbyDTO);
    }

    @Override
    public void replaceRules(Long roomId, String roomCode, List<TambolaRuleConfigRequestDTO> rules) {

        log.info("[TAMBOLA_ENGINE_RULES_PUT] Room:{} RoomCode:{} RuleCount:{}",
                roomId, roomCode, rules != null ? rules.size() : 0);

        feignClient.replaceRules(
                new TambolaReplaceRulesRequestDTO(roomId, roomCode, rules));

        log.info("[TAMBOLA_ENGINE_RULES_SYNCED] Room:{} RoomCode:{} RuleCount:{}",
                roomId, roomCode, rules != null ? rules.size() : 0);
    }
}