package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.constant.LudoErrorCodesEnum;
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
import com.codemonks.ludo_engine.exception.ResourceNotFoundException;
import com.codemonks.ludo_engine.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.codemonks.ludo_engine.constant.LudoErrorCodesEnum.INVALID_MOVE;
@Slf4j
@Service
@RequiredArgsConstructor
public class EngineServiceImpl implements EngineService {

    private final GameSetupService gameSetupService;
    private final TurnValidationService turnValidationService;
    private final MoveValidationService moveValidationService;
    private final TokenMovementService tokenMovementService;
    private final KillRuleService killRuleService;
    private final WinConditionService winConditionService;
    private final TurnRotationService turnRotationService;
    private final EventService eventService;
    private final ObjectMapper objectMapper;
    private final SupabaseRealtimeService supabaseRealtimeService;
    private final ExtraTurnService extraTurnService;
    private final AvailableMoveService availableMoveService;

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

        //Wrap game state in "board" for frontend, persist to Supabase
        Map<String, Object> startBoardWrapped = new HashMap<>();
        startBoardWrapped.put("board", response.getGameState());
        RealtimeGameStateDTO initialRealtimeState = RealtimeGameStateDTO.builder()
                .roomId(request.getRoomId())
                .roomCode(request.getRoomCode())
                .gameState(startBoardWrapped)
                .players(initializedGameState.getPlayers())
                .currentTurnUserId(initializedGameState.getCurrentTurnPlayerId())
                .gameStatus(GameStatusEnum.RUNNING.name())
                .winnerUserId(null)
                .botDifficulty(request.getBotDifficulty())
                .build();
        supabaseRealtimeService.upsertGameState(initialRealtimeState);
        log.info(
                "[INITIAL_STATE_PERSISTED] Room:{}",
                request.getRoomCode()
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
                "[STATE_LOADED] Room:{} CurrentTurn:{} Status:{}",
                realtimeState.getRoomId(),
                realtimeState.getCurrentTurnUserId(),
                realtimeState.getGameStatus()
        );

        Map<String, Object> rawState = realtimeState.getGameState();
        Object boardObj = rawState.get("board");
        Map<String, Object> gameStateMap = boardObj instanceof Map ? (Map<String, Object>) boardObj : rawState;
        GameStateDTO gameState = objectMapper.convertValue(gameStateMap, GameStateDTO.class);

        for (PlayerDTO player : gameState.getPlayers()) {
            for (TokenDTO token : player.getTokens()) {
                if (Boolean.TRUE.equals(token.getTokenKilled())) {
                    token.setTokenKilled(false);
                    token.setBackwardJourney(new ArrayList<>());
                }
            }
        }

        Long tokenId = getTokenId(request);
        Integer consumedDice = ((Number) request.getMoveData()
                        .get("consumedDice"))
                        .intValue();
        log.info("[MOVE_REQUEST] Room:{} Player:{} Token:{}",
                request.getRoomId(),
                request.getUserId(),
                tokenId
        );
        // Turn Validation
        turnValidationService.validateTurn(gameState, request.getUserId());
        if (gameState.getPlayerTurnStage() != PlayerTurnStageEnum.TOKEN_MOVE) {
            log.warn("[INVALID_TURN] RoomId:{} | PlayerID:{} attempted action during wrong stage",
                    request.getRoomId(), request.getUserId());
            throw new InvalidMoveException(LudoErrorCodesEnum.DICE_NOT_ROLLED);
        }

        // Move Validation
        moveValidationService.validateMove(gameState, request.getUserId(),
                tokenId,
                consumedDice);

        log.info("[MOVE_VALIDATED] Player:{} Token:{}", request.getUserId(), tokenId);
        GameStateDTO updatedGameState = tokenMovementService.moveToken(gameState,
                        request.getUserId(), tokenId,
                        consumedDice);

        log.info("[TOKEN_MOVED] Player:{} Token:{}", request.getUserId(), tokenId);

        List<EventDTO> events = new ArrayList<>();
        events.add(eventService.createEvent(EventTypeEnum.TOKEN_MOVED,
                "Player " + request.getUserId() + " moved token " + tokenId));

        KillRuleResultDTO killResult = killRuleService.processKillRule(updatedGameState,
                        request.getUserId(),
                        tokenId);

        updatedGameState = killResult.getGameState();

        if (killResult.isTokenKilled()) {
            events.add(eventService.createEvent(EventTypeEnum.TOKEN_KILLED,
                    "Player " + request.getUserId() + " killed opponent token"));
        }

        boolean tokenFinished = false;
        for (PlayerDTO player : updatedGameState.getPlayers()) {
            if (!player.getPlayerId().equals(request.getUserId())) {
                continue;}
            for (TokenDTO token : player.getTokens()) {
                if (token.getTokenId().equals(tokenId)) {
                    tokenFinished = token.getState() == TokenStateEnum.FINISHED;
                    break;
                }
            }
            break;
        }

        if (tokenFinished) {
            events.add(eventService.createEvent(EventTypeEnum.TOKEN_REACHED_HOME,
                            "Player " + request.getUserId() + " reached home"));
        }

        boolean extraTurn = extraTurnService.hasExtraTurn(
                        killResult.isTokenKilled(),
                        tokenFinished);

        PlayerDTO moverPlayer = null;

        for (PlayerDTO player : updatedGameState.getPlayers()) {
            if (player.getPlayerId().equals(request.getUserId())) {
                moverPlayer = player;
                break;
            }
        }

        if (moverPlayer != null
                && (killResult.isTokenKilled() || tokenFinished)) {

            moverPlayer.setPendingExtraTurn(true);

            log.info(
                    "[BONUS_STORED] Player:{} Bonus turn earned from kill/home.",
                    request.getUserId());
        }

        if (moverPlayer != null
                && moverPlayer.getPendingDice() != null
                && !moverPlayer.getPendingDice().isEmpty()) {
            List<LegalMoveDTO> remainingLegalMoves =
                    availableMoveService.getAvailableMoves(
                            updatedGameState,
                            moverPlayer,
                            moverPlayer.getPendingDice()
                    );

            if (remainingLegalMoves.isEmpty()) {
                log.info(
                        "[NO_LEGAL_MOVE_FOR_PENDING_DICE] Player:{} PendingDice:{} — clearing dice buffer",
                        request.getUserId(),
                        moverPlayer.getPendingDice()
                );
                moverPlayer.getPendingDice().clear();
            }
        }
        boolean effectiveExtraTurn = moverPlayer != null
                && (moverPlayer.getPendingDice() == null || moverPlayer.getPendingDice().isEmpty())
                && Boolean.TRUE.equals(moverPlayer.getPendingExtraTurn());

        if (effectiveExtraTurn) {
            moverPlayer.setPendingExtraTurn(false);
            log.info("[BONUS_CONSUMED] Player:{} — granting fresh reroll", request.getUserId());
        }
        updatedGameState = turnRotationService.updateTurn(
                        updatedGameState,
                        request.getUserId(),
                        effectiveExtraTurn
                );

        PlayerDTO nextTurnPlayer = null;
        for (PlayerDTO player : updatedGameState.getPlayers()) {
            if (player.getPlayerId().equals(updatedGameState.getCurrentTurnPlayerId())) {
                nextTurnPlayer = player;
                break;
            }
        }

        if (nextTurnPlayer != null
                && nextTurnPlayer.getPendingDice() != null
                && !nextTurnPlayer.getPendingDice().isEmpty()) {

            updatedGameState.setPlayerTurnStage(
                    PlayerTurnStageEnum.TOKEN_MOVE
            );

        } else {

            updatedGameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);
        }

        log.info(
                "[NEXT_TURN_STATE] Turn:{} Stage:{} PendingDice:{}",
                updatedGameState.getCurrentTurnPlayerId(),
                updatedGameState.getPlayerTurnStage(),
                nextTurnPlayer != null
                        ? nextTurnPlayer.getPendingDice()
                        : null
        );

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

        List<LegalMoveDTO> legalMoves =
                (nextTurnPlayer != null
                        && nextTurnPlayer.getPendingDice() != null)
                        ? availableMoveService.getAvailableMoves(
                        updatedGameState,
                        nextTurnPlayer,
                        nextTurnPlayer.getPendingDice())
                        : new ArrayList<>();

        log.info("[BEFORE_PERSIST] Turn:{} Stage:{}",
                updatedGameState.getCurrentTurnPlayerId(),
                updatedGameState.getPlayerTurnStage());

        Map<String, Object> persistedStateMap = objectMapper.convertValue(updatedGameState, Map.class);
        persistedStateMap.put("events", events);
        persistedStateMap.put("legalMoves", legalMoves);

        log.info("[MAP_STAGE] {}", persistedStateMap.get("playerTurnStage"));
        EngineGameStateResponseDTO response = new EngineGameStateResponseDTO();
        response.setGameState(persistedStateMap);
        response.setCurrentTurnUserId(updatedGameState.getCurrentTurnPlayerId());

        if (winnerResponse.getWinnerUserId() != null) {
            response.setStatus(GameStatusEnum.WIN);
            response.setWinnerUserId(winnerResponse.getWinnerUserId());
        } else {
            response.setStatus(GameStatusEnum.RUNNING);
        }

        Map<String, Object> moveBoardWrapped = new HashMap<>();
        moveBoardWrapped.put("board", persistedStateMap);
        RealtimeGameStateDTO updatedRealtimeState = RealtimeGameStateDTO.builder()
                .roomId(request.getRoomId())
                .roomCode(request.getRoomCode())
                .gameState(moveBoardWrapped)
                .players(updatedGameState.getPlayers())
                .currentTurnUserId(updatedGameState.getCurrentTurnPlayerId())
                .gameStatus(response.getStatus().name())
                .winnerUserId(response.getWinnerUserId())
                .build();
        supabaseRealtimeService.upsertGameState(updatedRealtimeState);
        log.info(
                "[STATE_PERSISTED] Room:{}",
                request.getRoomId()
        );

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

    @Override
    public DiceRollResponseDTO rollDice(DiceRollRequestDTO request) {
        log.info(
                "[ROLL_REQUEST] Room:{} Player:{}",
                request.getRoomId(),
                request.getPlayerId()
        );


        RealtimeGameStateDTO realtimeState = supabaseRealtimeService.getGameState(request.getRoomId());
        Map<String, Object> rawState = realtimeState.getGameState();
        Object boardObj = rawState.get("board");
        Map<String, Object> gameStateMap = boardObj instanceof Map ? (Map<String, Object>) boardObj : rawState;

        log.info("[GAME_STATE_FROM_DB] {}", gameStateMap);

        GameStateDTO gameState = objectMapper.convertValue(gameStateMap, GameStateDTO.class);

        for (PlayerDTO player : gameState.getPlayers()) {
            for (TokenDTO token : player.getTokens()) {
                if (Boolean.TRUE.equals(token.getTokenKilled())) {
                    token.setTokenKilled(false);
                    token.setBackwardJourney(new ArrayList<>());
                }
            }
        }
        log.info(
                "[STATE_LOADED] Room:{} Turn:{} Stage:{}",
                realtimeState.getRoomId(),
                gameState.getCurrentTurnPlayerId(),
                gameState.getPlayerTurnStage()
        );


        if (!gameState.getCurrentTurnPlayerId().equals(request.getPlayerId())) {
            log.warn(
                    "[INVALID_DICE_ROLL] Room:{} ExpectedPlayer:{} ActualPlayer:{}",
                    request.getRoomId(),
                    gameState.getCurrentTurnPlayerId(),
                    request.getPlayerId()
            );
            throw new InvalidMoveException(LudoErrorCodesEnum.INVALID_TURN);
        }

        if (gameState.getPlayerTurnStage() != PlayerTurnStageEnum.ROLL_DICE) {

            log.warn(
                    "[INVALID_STAGE] Room:{} Player:{} CurrentStage:{}",
                    request.getRoomId(),
                    request.getPlayerId(),
                    gameState.getPlayerTurnStage()
            );

            throw new InvalidMoveException(LudoErrorCodesEnum.DICE_ALREADY_ROLLED);

        }

        PlayerDTO currentPlayer = null;
        for (PlayerDTO player : gameState.getPlayers()) {
            if (player.getPlayerId().equals(request.getPlayerId())) {
                currentPlayer = player;
                break;
            }
        }

        if (currentPlayer == null) {
            throw new ResourceNotFoundException(LudoErrorCodesEnum.PLAYER_NOT_FOUND);
        }

        int diceNumber = ThreadLocalRandom.current().nextInt(1, 7);
        log.info("[DICE_GENERATED] DiceNumber:{}", diceNumber);


        gameState.setLastDice(diceNumber);
        gameState.setLastDicePlayerId(request.getPlayerId());

        currentPlayer.getPendingDice().add(diceNumber);
        log.info(
                "[DICE_BUFFER] Player:{} PendingDice:{}",
                request.getPlayerId(),
                currentPlayer.getPendingDice()
        );
        List<Integer> pendingDice = currentPlayer.getPendingDice();
        boolean autoSkipHandled = false;

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

        if (!tripleSixForfeited) {
            List<LegalMoveDTO> combinedLegalMoves =
                    availableMoveService.getAvailableMoves(
                            gameState,
                            currentPlayer,
                            currentPlayer.getPendingDice()
                    );
            boolean anyDiceUsable = !combinedLegalMoves.isEmpty();

            if (diceNumber == 6) {
                gameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);
                log.info("[ROLLED_6] Extra roll granted. PlayerId:{} Buffer:{}",
                        request.getPlayerId(), pendingDice);

            } else if (anyDiceUsable) {
                gameState.setPlayerTurnStage(PlayerTurnStageEnum.TOKEN_MOVE);
                log.info("[STAGE_MOVE] Player:{} Dice:{}. Stage:TOKEN_MOVE. Buffer:{}",
                        request.getPlayerId(), diceNumber, pendingDice);

            } else {
                currentPlayer.getPendingDice().clear();
                autoSkipHandled = true;

                log.info(
                        "[NO_MOVE] Player:{} Dice:{} Buffer:{} — nothing usable, rotating turn now.",
                        request.getPlayerId(), diceNumber, pendingDice
                );

                gameState = turnRotationService.updateTurn(
                        gameState,
                        request.getPlayerId(),
                        false
                );
                gameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);
                Map<String, Object> immediateStateMap = objectMapper.convertValue(gameState, Map.class);
                immediateStateMap.put("legalMoves", new ArrayList<LegalMoveDTO>());

                Map<String, Object> immediateBoardWrapped = new HashMap<>();
                immediateBoardWrapped.put("board", immediateStateMap);

                RealtimeGameStateDTO immediateRealtimeState = RealtimeGameStateDTO.builder()
                        .roomId(realtimeState.getRoomId())
                        .roomCode(realtimeState.getRoomCode())
                        .gameState(immediateBoardWrapped)
                        .players(gameState.getPlayers())
                        .currentTurnUserId(gameState.getCurrentTurnPlayerId())
                        .gameStatus(realtimeState.getGameStatus())
                        .winnerUserId(realtimeState.getWinnerUserId())
                        .build();
                supabaseRealtimeService.upsertGameState(immediateRealtimeState);
                log.info("[NO_MOVE_TURN_ROTATED] Room:{} FromPlayer:{} ToPlayer:{}",
                        request.getRoomId(), request.getPlayerId(), gameState.getCurrentTurnPlayerId());
            }
        }

        Map<String, Object> diceStateMap;
        if (!autoSkipHandled) {

            List<LegalMoveDTO> legalMoves = availableMoveService.getAvailableMoves(
                            gameState,
                            currentPlayer,
                            currentPlayer.getPendingDice()
                    );
            diceStateMap = objectMapper.convertValue(gameState, Map.class);

            diceStateMap.put("legalMoves", legalMoves);

            Map<String, Object> diceBoardWrapped = new HashMap<>();
            diceBoardWrapped.put("board", diceStateMap);
            RealtimeGameStateDTO updatedRealtimeState = RealtimeGameStateDTO.builder()
                    .roomId(realtimeState.getRoomId())
                    .roomCode(realtimeState.getRoomCode())
                    .gameState(diceBoardWrapped)
                    .players(gameState.getPlayers())
                    .currentTurnUserId(gameState.getCurrentTurnPlayerId())
                    .gameStatus(realtimeState.getGameStatus())
                    .winnerUserId(realtimeState.getWinnerUserId())
                    .build();
            supabaseRealtimeService.upsertGameState(updatedRealtimeState);
            log.info(
                    "[DICE_STATE_PERSISTED] Room:{}",
                    request.getRoomId()
            );
        }
        else {
            diceStateMap = objectMapper.convertValue(gameState, Map.class);
            diceStateMap.put("legalMoves", new ArrayList<LegalMoveDTO>());
        }
            // ── Build Response
            DiceRollResponseDTO response = new DiceRollResponseDTO();
            response.setRoomId(request.getRoomId());
            response.setPlayerId(request.getPlayerId());
            response.setDice(diceNumber);
            response.setPendingDice(currentPlayer.getPendingDice());
            response.setGameState(diceStateMap);
            response.setCurrentTurnPlayerId(gameState.getCurrentTurnPlayerId());
            response.setPlayerTurnStage(gameState.getPlayerTurnStage());
            response.setTripleSixForfeited(tripleSixForfeited);

        response.setDelayedTurnRotationRequired(autoSkipHandled);

            log.info(
                    "[ROLL_COMPLETED] Player:{} Dice:{} NextTurn:{} Stage:{}",
                    request.getPlayerId(),
                    diceNumber,
                    gameState.getCurrentTurnPlayerId(),
                    gameState.getPlayerTurnStage()
            );
            return response;
        }

    @Override
    public GameStateDTO continueTurnAfterDelay(Long roomId, String roomCode, Long playerId) {

        log.info("[DELAYED_TURN_CONTINUATION] Room:{} Player:{}", roomId, playerId);

        // Reload the latest authoritative state
        RealtimeGameStateDTO realtimeState = supabaseRealtimeService.getGameState(roomId);

        Map<String, Object> rawState = realtimeState.getGameState();

        Object boardObj = rawState.get("board");

        Map<String, Object> gameStateMap =
                boardObj instanceof Map
                        ? (Map<String, Object>) boardObj
                        : rawState;

        GameStateDTO gameState = objectMapper.convertValue(
                        gameStateMap,
                        GameStateDTO.class
                );

        if (!playerId.equals(gameState.getCurrentTurnPlayerId())) {

            log.warn(
                    "[DELAYED_TURN_SKIPPED] Room:{} ExpectedPlayer:{} CurrentPlayer:{}",
                    roomId,
                    playerId,
                    gameState.getCurrentTurnPlayerId()
            );

            return null;
        }

        gameState = turnRotationService.updateTurn(gameState, playerId, false);

        gameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);

        Map<String, Object> persistedStateMap = objectMapper.convertValue(
                        gameState,
                        Map.class);
        persistedStateMap.put("legalMoves", new ArrayList<LegalMoveDTO>());
        Map<String, Object> boardWrapped = new HashMap<>();
        boardWrapped.put("board", persistedStateMap);

        RealtimeGameStateDTO updatedRealtimeState = RealtimeGameStateDTO.builder().roomId(roomId)
                        .roomCode(
                                roomCode != null
                                        ? roomCode
                                        : realtimeState.getRoomCode()
                        )
                        .gameState(boardWrapped)
                        .players(gameState.getPlayers())
                        .currentTurnUserId(gameState.getCurrentTurnPlayerId())
                        .gameStatus(realtimeState.getGameStatus())
                        .winnerUserId(realtimeState.getWinnerUserId())
                        .build();

        supabaseRealtimeService.upsertGameState(updatedRealtimeState);
        log.info(
                "[DELAYED_TURN_ROTATED] Room:{} FromPlayer:{} ToPlayer:{}",
                roomId,
                playerId,
                gameState.getCurrentTurnPlayerId()
        );
        return gameState;
    }
    private Long getTokenId(EngineMoveRequestDTO request) {
        Object value = request.getMoveData().get("tokenId");
        if (value == null) {
            throw new InvalidMoveException(INVALID_MOVE);
        }
        return ((Number) value).longValue();
    }
}