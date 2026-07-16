package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.constant.BoardConstants;
import com.codemonks.ludo_engine.dto.common.*;
import com.codemonks.ludo_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.ludo_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.ludo_engine.dto.realtime.RealtimeMoveDTO;
import com.codemonks.ludo_engine.dto.request.DiceRollRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.ludo_engine.dto.response.DiceRollResponseDTO;
import com.codemonks.ludo_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.ludo_engine.enums.EventTypeEnum;
import com.codemonks.ludo_engine.enums.GameStatusEnum;
import com.codemonks.ludo_engine.enums.PlayerTurnStageEnum;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.codemonks.ludo_engine.constant.ErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class EngineServiceImpl implements EngineService {

    private final GameSetupService gameSetupService;
    private final TurnValidationService turnValidationService;
    private final MoveValidationService moveValidationService;
    private final TokenMovementService tokenMovementService;
    private final KillRuleService killRuleService;
    private final HomeRuleService homeRuleService;
    private final WinConditionService winConditionService;
    private final TurnRotationService turnRotationService;
    private final EventService eventService;
    private final ObjectMapper objectMapper;
    private final SupabaseRealtimeService supabaseRealtimeService;

    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request) {

        log.info(
                "[GAME_START_REQUEST] Room:{} Players:{}",
                request.getRoomCode(),
                request.getPlayerIds()
        );

        GameStateDTO initializedGameState = gameSetupService.initializeGame(request);

        log.info(
                "[FIRST_TURN_SELECTED] Room:{} FirstPlayer:{}",
                request.getRoomCode(),
                initializedGameState.getCurrentTurnPlayerId()
        );
        EngineGameStateResponseDTO response = new EngineGameStateResponseDTO();
        response.setGameState(objectMapper.convertValue(initializedGameState, Map.class));
        response.setCurrentTurnUserId(initializedGameState.getCurrentTurnPlayerId());
        response.setStatus(GameStatusEnum.RUNNING);
        response.setWinnerUserId(null);
        response.setPlayers(initializedGameState.getPlayers());
        response.setBotDifficulty(request.getBotDifficulty());

        // ── Persist initial state to Supabase
        RealtimeGameStateDTO initialRealtimeState = RealtimeGameStateDTO.builder()
                .roomId(request.getRoomId())
                .roomCode(request.getRoomCode())
                .gameState(response.getGameState())
                .currentTurnUserId(initializedGameState.getCurrentTurnPlayerId())
                .gameStatus(GameStatusEnum.RUNNING.name())
                .winnerUserId(null)
                .version(1L)
                .build();
        supabaseRealtimeService.upsertGameState(initialRealtimeState);
        log.info(
                "[INITIAL_STATE_PERSISTED] Room:{} Version:{}",
                request.getRoomCode(),
                1L
        );
        log.info(
                "[GAME_STARTED] Room:{} CurrentTurn:{}",
                request.getRoomCode(),
                initializedGameState.getCurrentTurnPlayerId()
        );
        return response;
    }
    @Override
    public void publishLobbyState(RealtimeLobbyDTO lobbyDTO) {
        supabaseRealtimeService.publishLobbyState(lobbyDTO);
        log.info("[LOBBY_STATE_DELEGATED] roomId={}", lobbyDTO.getRoomId());
    }

    @Override
    public EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request) {

        RealtimeGameStateDTO realtimeState = supabaseRealtimeService.getGameState(request.getRoomId());

        log.info(
                "[STATE_LOADED] Room:{} Version:{} CurrentTurn:{} Status:{}",
                realtimeState.getRoomId(),
                realtimeState.getVersion(),
                realtimeState.getCurrentTurnUserId(),
                realtimeState.getGameStatus()
        );

        GameStateDTO gameState = objectMapper.convertValue(realtimeState.getGameState(), GameStateDTO.class);


        Long tokenId = getTokenId(request);
        // NEW - Extract consumed dice from request once
        Integer consumedDice =
                ((Number) request.getMoveData()
                        .get("consumedDice"))
                        .intValue();
        log.info(
                "[MOVE_REQUEST] Room:{} Player:{} Token:{}",
                request.getRoomId(),
                request.getUserId(),
                tokenId
        );

        // Turn Validation
        turnValidationService.validateTurn(gameState, request.getUserId());
        if (gameState.getPlayerTurnStage() != PlayerTurnStageEnum.TOKEN_MOVE) {
            log.warn("[INVALID_TURN] RoomId:{} | PlayerID:{} attempted action during wrong stage",
                    request.getRoomId(), request.getUserId());
            throw new InvalidMoveException(INVALID_MOVE);
        }

        // Move Validation
        moveValidationService.validateMove(
                gameState,
                request.getUserId(),
                tokenId,
                consumedDice
        );

        log.info(
                "[MOVE_VALIDATED] Player:{} Token:{}",
                request.getUserId(),
                tokenId
        );
        // ── Token Movement
        // CHANGED - new service signature
        GameStateDTO updatedGameState =
                tokenMovementService.moveToken(
                        gameState,
                        request.getUserId(),
                        tokenId,
                        consumedDice
                );

        log.info(
                "[TOKEN_MOVED] Player:{} Token:{}",
                request.getUserId(),
                tokenId
        );

        List<EventDTO> events = new ArrayList<>();
        events.add(eventService.createEvent(EventTypeEnum.TOKEN_MOVED,
                "Player " + request.getUserId() + " moved token " + tokenId));

        // ── Kill Rule

        KillRuleResultDTO killResult =
                killRuleService.processKillRule(
                        updatedGameState,
                        request.getUserId(),
                        tokenId
                );

        updatedGameState = killResult.getGameState();

        if (killResult.isTokenKilled()) {
            events.add(eventService.createEvent(EventTypeEnum.TOKEN_KILLED,
                    "Player " + request.getUserId() + " killed opponent token"));
        }

        // ── Home Rule
        // CHANGED - migrated to DTO flow
        HomeRuleResultDTO homeResult =
                homeRuleService.processHomeRule(
                        updatedGameState,
                        request.getUserId(),
                        tokenId
                );

        updatedGameState = homeResult.getGameState();

        if (homeResult.isReachedHome()) {
            events.add(eventService.createEvent(EventTypeEnum.TOKEN_REACHED_HOME,
                    "Player " + request.getUserId() + " reached home"));
        }

        // ── Winner Check
        EngineGameStateResponseDTO winnerResponse = winConditionService.checkWinner(
                updatedGameState, request.getUserId());
        if (winnerResponse.getWinnerUserId() != null) {
            log.info(
                    "[WINNER_DECLARED] Room:{} Winner:{}",
                    request.getRoomId(),
                    winnerResponse.getWinnerUserId()
            );
        }
        // ── Extra Turn Logic
        boolean extraTurn = killResult.isTokenKilled() || homeResult.isReachedHome();

        // ── Stage Decision
        PlayerDTO currentPlayer = null;
        for (PlayerDTO player : updatedGameState.getPlayers()) {
            if (player.getPlayerId().equals(request.getUserId())) {
                currentPlayer = player;
                break;
            }
        }

        if (currentPlayer != null
                && currentPlayer.getPendingDice() != null
                && !currentPlayer.getPendingDice().isEmpty()) {
            updatedGameState.setPlayerTurnStage(PlayerTurnStageEnum.TOKEN_MOVE);
            log.info("[STAGE RETAINED] Player:{} buffer:{} Stage:TOKEN_MOVE",
                    request.getUserId(), currentPlayer.getPendingDice());
        } else {
            updatedGameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);
            log.info("[STAGE RESET] Player:{} buffer empty. Stage:ROLL_DICE", request.getUserId());
        }

        // ── Turn Update
        updatedGameState = turnRotationService.updateTurn(
                updatedGameState, request.getUserId(), extraTurn);

        if (extraTurn) {
            events.add(eventService.createEvent(EventTypeEnum.EXTRA_TURN,
                    "Player " + request.getUserId() + " received extra turn"));
        }

        // ── Build Response — separate maps so the durable Supabase state never carries the transient events list
        Map<String, Object> persistedStateMap = objectMapper.convertValue(updatedGameState, Map.class);
        Map<String, Object> responseStateMap = new HashMap<>(persistedStateMap);
        responseStateMap.put("events", events);

        EngineGameStateResponseDTO response = new EngineGameStateResponseDTO();
        response.setGameState(responseStateMap);
        response.setCurrentTurnUserId(updatedGameState.getCurrentTurnPlayerId());

        if (winnerResponse.getWinnerUserId() != null) {
            response.setStatus(GameStatusEnum.WIN);
            response.setWinnerUserId(winnerResponse.getWinnerUserId());
        } else {
            response.setStatus(GameStatusEnum.RUNNING);
        }

        // ── Persist updated state
        RealtimeGameStateDTO updatedRealtimeState = RealtimeGameStateDTO.builder()
                .roomId(request.getRoomId())
                .roomCode(request.getRoomCode())
                .gameState(persistedStateMap)
                .currentTurnUserId(updatedGameState.getCurrentTurnPlayerId())
                .gameStatus(response.getStatus().name())
                .winnerUserId(response.getWinnerUserId())
                .version(realtimeState.getVersion() + 1)
                .build();
        supabaseRealtimeService.upsertGameState(updatedRealtimeState);
        log.info(
                "[STATE_PERSISTED] Room:{} Version:{}",
                request.getRoomId(),
                updatedRealtimeState.getVersion()
        );
        // ── Persist move history
        RealtimeMoveDTO moveDTO = RealtimeMoveDTO.builder()
                .roomId(request.getRoomId())
                .roomCode(request.getRoomCode())
                .playerId(request.getUserId())
                .moveData(request.getMoveData())
                .build();
        supabaseRealtimeService.saveMove(moveDTO);
        log.info(
                "[MOVE_COMPLETED] Room:{} Player:{} NextTurn:{}",
                request.getRoomId(),
                request.getUserId(),
                updatedGameState.getCurrentTurnPlayerId()
        );
        return response;
    }

    //Roll dice
    @Override
    public DiceRollResponseDTO rollDice(DiceRollRequestDTO request) {
        log.info(
                "[ROLL_REQUEST] Room:{} Player:{}",
                request.getRoomId(),
                request.getPlayerId()
        );

        // ── Fetch authoritative state from Supabase
        RealtimeGameStateDTO realtimeState = supabaseRealtimeService.getGameState(request.getRoomId());
        GameStateDTO gameState = objectMapper.convertValue(realtimeState.getGameState(), GameStateDTO.class);

        log.info(
                "[STATE_LOADED] Room:{} Version:{} Turn:{} Stage:{}",
                realtimeState.getRoomId(),
                realtimeState.getVersion(),
                gameState.getCurrentTurnPlayerId(),
                gameState.getPlayerTurnStage()
        );
        // Turn validation
        if (!gameState.getCurrentTurnPlayerId().equals(request.getPlayerId())) {
            log.warn(
                    "[INVALID_DICE_ROLL] Room:{} ExpectedPlayer:{} ActualPlayer:{}",
                    request.getRoomId(),
                    gameState.getCurrentTurnPlayerId(),
                    request.getPlayerId()
            );
            throw new IllegalStateException("Not your turn to roll dice");
        }

        // Stage validation
        if (gameState.getPlayerTurnStage() != PlayerTurnStageEnum.ROLL_DICE) {

            log.warn(
                    "[INVALID_STAGE] Room:{} Player:{} CurrentStage:{}",
                    request.getRoomId(),
                    request.getPlayerId(),
                    gameState.getPlayerTurnStage()
            );

            throw new IllegalStateException("Player must complete token movement first");

        }

        // Find current player
        PlayerDTO currentPlayer = null;
        for (PlayerDTO player : gameState.getPlayers()) {
            if (player.getPlayerId().equals(request.getPlayerId())) {
                currentPlayer = player;
                break;
            }
        }

        if (currentPlayer == null) {
            throw new IllegalStateException("Player not found");
        }

        // Roll dice
        int diceNumber = ThreadLocalRandom.current().nextInt(1, 7);
        log.info("[DICE_GENERATED] DiceNumber:{}", diceNumber);

        currentPlayer.getPendingDice().add(diceNumber);
        log.info(
                "[DICE_BUFFER] Player:{} PendingDice:{}",
                request.getPlayerId(),
                currentPlayer.getPendingDice()
        );
        List<Integer> pendingDice = currentPlayer.getPendingDice();

        // Tracks whether the turn was already forfeited by a triple-6, so the stage-decision
        // block below gets skipped — otherwise it would re-evaluate the same dice roll and
        // undo the forfeiture (flip stage back / rotate turn a second time).
        boolean tripleSixForfeited = false;

        // ── Triple 6 Check
        if (pendingDice.size() >= 3) {
            int size = pendingDice.size();
            boolean tripleSix = pendingDice.get(size - 1) == 6
                    && pendingDice.get(size - 2) == 6
                    && pendingDice.get(size - 3) == 6;

            if (tripleSix) {
                currentPlayer.getPendingDice().clear();
                gameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);
                turnRotationService.updateTurn(gameState, request.getPlayerId(), false);
                tripleSixForfeited = true;
                log.info("[TRIPLE_6] Turn Forfeited. PlayerId:{} | Next:{}",
                        request.getPlayerId(),
                        gameState.getCurrentTurnPlayerId());
            }
        }

        // ── Stage Decision after dice roll — skipped if triple-six already forfeited the turn
        if (!tripleSixForfeited) {
            boolean legalMoveExists = hasAnyLegalMove(currentPlayer, diceNumber);

            if (diceNumber == 6) {
                gameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);
                log.info("[ROLLED_6] Extra roll granted. PlayerId:{} Buffer:{}",
                        request.getPlayerId(), pendingDice);
            } else if (legalMoveExists || pendingDice.contains(6)) {
                gameState.setPlayerTurnStage(PlayerTurnStageEnum.TOKEN_MOVE);
                log.info("[STAGE_MOVE] Player:{} Dice:{}. Stage:TOKEN_MOVE. Buffer:{}",
                        request.getPlayerId(), diceNumber, pendingDice);
            } else {
                currentPlayer.getPendingDice().clear();
                gameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);
                turnRotationService.updateTurn(gameState, request.getPlayerId(), false);
                log.info("[NO_MOVE] PlayerId:{} Dice:{} No legal move. Next:{}",
                        request.getPlayerId(), diceNumber,
                        gameState.getCurrentTurnPlayerId());
            }
        }

        // ── Persist updated state to Supabase
        RealtimeGameStateDTO updatedRealtimeState = RealtimeGameStateDTO.builder()
                .roomId(realtimeState.getRoomId())
                .roomCode(realtimeState.getRoomCode())
                .gameState(objectMapper.convertValue(gameState, Map.class))
                .currentTurnUserId(gameState.getCurrentTurnPlayerId())
                .gameStatus(realtimeState.getGameStatus()) // dice rolls never change win/draw status
                .winnerUserId(realtimeState.getWinnerUserId())
                .version(realtimeState.getVersion() + 1)
                .build();
        supabaseRealtimeService.upsertGameState(updatedRealtimeState);
        log.info(
                "[DICE_STATE_PERSISTED] Room:{} Version:{}",
                request.getRoomId(),
                updatedRealtimeState.getVersion()
        );
        // ── Build Response
        DiceRollResponseDTO response = new DiceRollResponseDTO();
        response.setRoomId(request.getRoomId());
        response.setPlayerId(request.getPlayerId());
        response.setDice(diceNumber);
        response.setPendingDice(currentPlayer.getPendingDice());
        response.setGameState(objectMapper.convertValue(gameState, Map.class));
        response.setCurrentTurnPlayerId(gameState.getCurrentTurnPlayerId());
        response.setPlayerTurnStage(gameState.getPlayerTurnStage());
        response.setTripleSixForfeited(tripleSixForfeited);

        log.info(
                "[ROLL_COMPLETED] Player:{} Dice:{} NextTurn:{} Stage:{}",
                request.getPlayerId(),
                diceNumber,
                gameState.getCurrentTurnPlayerId(),
                gameState.getPlayerTurnStage()
        );
        return response;
    }


    private boolean hasAnyLegalMove(PlayerDTO player, int diceNumber) {
        for (TokenDTO token : player.getTokens()) {

            if (token.getState() == TokenStateEnum.BASE && diceNumber == 6) {
                return true;
            }

            if (token.getState() == TokenStateEnum.TRACK) {
                int trackStart = BoardConstants.TRACK_START.get(token.getColor());
                int effectiveDistance = (token.getPosition() - trackStart + BoardConstants.BOARD_SIZE)
                        % BoardConstants.BOARD_SIZE;
                int newEffectiveDistance = effectiveDistance + diceNumber;

                if (newEffectiveDistance >= BoardConstants.HOME_PATH_ENTRY_DISTANCE) {
                    int homePathPos = newEffectiveDistance - BoardConstants.HOME_PATH_ENTRY_DISTANCE;
                    if (homePathPos <= BoardConstants.HOME_PATH_SIZE - 1) {
                        return true;
                    }
                } else {
                    return true;
                }
            }

            if (token.getState() == TokenStateEnum.HOME_PATH) {
                int newPos = token.getPosition() + diceNumber;
                if (newPos <= BoardConstants.HOME_PATH_SIZE - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private Long getTokenId(EngineMoveRequestDTO request) {
        Object value = request.getMoveData().get("tokenId");
        if (value == null) {
            throw new InvalidMoveException(INVALID_MOVE);
        }
        return ((Number) value).longValue();
    }
}