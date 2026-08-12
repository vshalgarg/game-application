package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.bot.constants.BotConstants;
import com.codemonks.ludo_engine.bot.factory.BotStrategyFactory;
import com.codemonks.ludo_engine.bot.strategy.BotStrategy;
import com.codemonks.ludo_engine.constant.GameConstants;
import com.codemonks.ludo_engine.dto.common.BotDecisionDTO;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.request.DiceRollRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.ludo_engine.dto.response.DiceRollResponseDTO;
import com.codemonks.ludo_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.ludo_engine.enums.PlayerTurnStageEnum;
import com.codemonks.ludo_engine.service.BotMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotMoveServiceImpl implements BotMoveService {

    private final BotStrategyFactory botStrategyFactory;
    private final EngineServiceImpl engineService;
    private final ObjectMapper objectMapper;

    @Override
    @Async("botMoveExecutor")
    public void processBotTurn(
            GameStateDTO gameState,
            Long roomId,
            String roomCode,
            Long botPlayerId
    ) {

        Long activePlayerId = botPlayerId;
        boolean needsRoll = true;
        GameStateDTO currentState = gameState;

        // Loop internally: rolling, rerolling on 6, delayed no-move rotation,
        // playing out remaining pending dice, and bot-to-bot chaining — all
        // without leaving this class, so there's no dependency edge back to
        // BotTurnService/TurnDelayService.
        while (activePlayerId != null) {

            try {

                if (needsRoll) {

                    Thread.sleep(BotConstants.BOT_DELAY_MS);

                    DiceRollRequestDTO diceRequest = new DiceRollRequestDTO();
                    diceRequest.setRoomId(roomId);
                    diceRequest.setPlayerId(activePlayerId);

                    DiceRollResponseDTO diceResponse =
                            engineService.rollDice(diceRequest);

                    log.info(
                            "[BOT_DICE_RESPONSE] {}",
                            diceResponse.getGameState()
                    );

                    // No legal move for this roll: wait, rotate, then
                    // continue with whoever is next (if a bot).
                    if (diceResponse.isDelayedTurnRotationRequired()) {

                        log.info(
                                "[BOT_NO_MOVE_DELAYED_ROTATION] Room:{} Bot:{}",
                                roomCode,
                                activePlayerId
                        );

                        Thread.sleep(GameConstants.TURN_DELAY_MS);

                        GameStateDTO rotatedState =
                                engineService.continueTurnAfterDelay(
                                        roomId,
                                        roomCode,
                                        activePlayerId
                                );

                        activePlayerId = nextBotIdOrNull(rotatedState);
                        needsRoll = true;
                        continue;
                    }

                    // Rolled a 6 (or similar): stage stays ROLL_DICE without
                    // being a "no move" case. Same bot rolls again.
                    if (diceResponse.getPlayerTurnStage() != PlayerTurnStageEnum.TOKEN_MOVE) {

                        log.info(
                                "[BOT_TURN_COMPLETED_AFTER_ROLL] Room:{} Bot:{} Stage:{}",
                                roomCode,
                                activePlayerId,
                                diceResponse.getPlayerTurnStage()
                        );

                        needsRoll = true;
                        continue;
                    }

                    // Legal move exists: fall through to the move branch
                    // using this freshly rolled state, without rolling again.
                    currentState = objectMapper.convertValue(
                            diceResponse.getGameState(),
                            GameStateDTO.class
                    );
                    needsRoll = false;
                }

                // ---- Move branch: play the current pending dice buffer ----

                PlayerDTO botPlayerState = findPlayer(currentState, activePlayerId);

                if (botPlayerState == null) {
                    log.warn(
                            "[BOT_PLAYER_NOT_FOUND] Room:{} Bot:{}",
                            roomCode,
                            activePlayerId
                    );
                    return;
                }

                BotStrategy strategy =
                        botStrategyFactory.getStrategy(
                                currentState,
                                activePlayerId
                        );

                BotDecisionDTO decision =
                        strategy.chooseMove(
                                currentState,
                                activePlayerId,
                                botPlayerState.getPendingDice()
                        );

                if (!decision.isMoveAvailable()) {

                    log.info(
                            "[BOT_NO_MOVE_AVAILABLE] Room:{} Bot:{}",
                            roomCode,
                            activePlayerId
                    );

                    return;
                }

                Map<String, Object> moveData = new HashMap<>();
                moveData.put("tokenId", decision.getMove().getTokenId());
                moveData.put("consumedDice", decision.getMove().getDice());

                EngineMoveRequestDTO moveRequest = new EngineMoveRequestDTO();
                moveRequest.setRoomId(roomId);
                moveRequest.setRoomCode(roomCode);
                moveRequest.setUserId(activePlayerId);
                moveRequest.setMoveData(moveData);

                log.info(
                        "[BOT_EXECUTING_MOVE] Room:{} Bot:{} Token:{} Dice:{}",
                        roomCode,
                        activePlayerId,
                        decision.getMove().getTokenId(),
                        decision.getMove().getDice()
                );

                EngineGameStateResponseDTO moveResponse =
                        engineService.processMove(moveRequest);

                GameStateDTO afterMoveState =
                        objectMapper.convertValue(
                                moveResponse.getGameState(),
                                GameStateDTO.class
                        );

                if (activePlayerId.equals(afterMoveState.getCurrentTurnPlayerId())) {

                    // Still this bot's turn: either more pending dice to
                    // play (TOKEN_MOVE), or it earned a fresh roll (ROLL_DICE).
                    needsRoll = afterMoveState.getPlayerTurnStage()
                            != PlayerTurnStageEnum.TOKEN_MOVE;
                    currentState = afterMoveState;

                } else {

                    // Turn moved on: continue only if the next player is a bot.
                    activePlayerId = nextBotIdOrNull(afterMoveState);
                    needsRoll = true;
                }

            } catch (Exception exception) {

                log.error(
                        "[BOT_MOVE_FAILED] Room:{} Bot:{}",
                        roomCode,
                        activePlayerId,
                        exception
                );

                return;
            }
        }
    }

    private PlayerDTO findPlayer(GameStateDTO gameState, Long playerId) {

        if (gameState == null || gameState.getPlayers() == null) {
            return null;
        }

        return gameState.getPlayers().stream()
                .filter(player -> playerId.equals(player.getPlayerId()))
                .findFirst()
                .orElse(null);
    }

    private Long nextBotIdOrNull(GameStateDTO gameState) {

        if (gameState == null
                || gameState.getPlayers() == null
                || gameState.getCurrentTurnPlayerId() == null) {

            return null;
        }

        return gameState.getPlayers().stream()
                .filter(player ->
                        gameState.getCurrentTurnPlayerId().equals(player.getPlayerId())
                )
                .filter(player -> Boolean.TRUE.equals(player.getIsBot()))
                .map(PlayerDTO::getPlayerId)
                .findFirst()
                .orElse(null);
    }
}