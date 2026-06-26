package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.bot.constants.BotConstants;
import com.codemonks.tic_tac_toe_game_engine.bot.enums.BotDifficultyEnum;
import com.codemonks.tic_tac_toe_game_engine.bot.enums.MatchTypeEnum;
import com.codemonks.tic_tac_toe_game_engine.bot.factory.BotStrategyFactory;
import com.codemonks.tic_tac_toe_game_engine.bot.strategy.BotStrategy;
import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import com.codemonks.tic_tac_toe_game_engine.exception.TicTacToeEngineException;
import com.codemonks.tic_tac_toe_game_engine.mapper.BoardMapper;
import com.codemonks.tic_tac_toe_game_engine.mapper.MoveMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codemonks.tic_tac_toe_game_engine.constant.EngineErrorCodesEnum.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicTacToeEngineImpl implements TicTacToeEngine {

    private final MoveProcessorService moveProcessorService;
    private final BotStrategyFactory botFactory;
    private final SupabaseRealtimeService supabaseRealtimeService;
    private final ObjectMapper objectMapper;
 @Autowired
 @Lazy
    private  BotMoveService botMoveService;


    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request) {

        log.info("Initializing new game for room: {}", request.getRoomCode());
        Board board = new Board();
        log.debug("[START_GAME] 3x3 board initialized successfully");

        // Assign Players
        List<PlayerDTO> players = buildPlayers(request);

        log.info(
                "[START_GAME] Match initialized | Room: {} | MatchType: {} | Players: {} vs {}",
                request.getRoomCode(),
                request.getMatchType(),
                players.get(0).getUserId(),
                players.get(1).getUserId()
        );

        BotDifficultyEnum difficulty = request.getBotDifficulty();

        Map<String, Object> gameState =
        moveProcessorService.buildGameState(board, players, difficulty);

        EngineGameStateResponseDTO response =
                EngineGameStateResponseDTO.builder()
                        .gameState(gameState)
                        .currentTurnUserId(players.get(0).getUserId())
                        .status(GameStatusEnum.INITIALIZED)
                        .winnerUserId(null)
                        .players(players)
                        //.botDifficulty(getBotDifficultyFromGameState(gameState))
                        .botDifficulty(request.getBotDifficulty())
                        .build();

        RealtimeGameStateDTO realtimeState =
                RealtimeGameStateDTO.builder()
                        .roomId(request.getRoomId())
                        .roomCode(request.getRoomCode())
                        .gameState(response.getGameState())
                        .currentTurnUserId(response.getCurrentTurnUserId())
                        .gameStatus(response.getStatus().name())
                        .winnerUserId(null)
                        .stateSequence(1L)
                        .build();

        supabaseRealtimeService.upsertGameState(realtimeState);
        log.info(
                "[INITIAL_STATE_PERSISTED] Room:{} Version:{}",
                request.getRoomCode(),
                1L
        );
        log.info("Top level players = {}", response.getPlayers());

        return response;
    }

        @Override
    public EngineGameStateResponseDTO makeMove(EngineMoveRequestDTO request) {

        log.info(
                "[MAKE_MOVE] Processing move for user: {} in room: {}",
                request.getUserId(),
                request.getRoomId()
        );


        RealtimeGameStateDTO realtimeState =
                supabaseRealtimeService.getGameState(
                        request.getRoomId()
                );

        Map<String, Object> gameState =
                realtimeState.getGameState();

        Board board = BoardMapper.toDomain(gameState);

        List<PlayerDTO> players =
                extractPlayersFromGameState(gameState);
            log.info("Players extracted = {}", players);

        //BOT MOVE (called by BotMoveService async thread
        if (BotConstants.BOT_USER_ID.equals(request.getUserId())) {

            PlayerDTO botPlayer = getPlayerByUserId(players, BotConstants.BOT_USER_ID);
            CellValue botSymbol = CellValue.valueOf(botPlayer.getSide());

            BotStrategy strategy = botFactory.getStrategy(getBotDifficultyFromGameState(gameState));
            Move botMove = strategy.chooseMove(board, botSymbol);
            moveProcessorService.applyMove(board, botMove, botSymbol);

            log.info("[BOT_MOVE] {} bot played at [{},{}]",
                    getBotDifficultyFromGameState(gameState), botMove.getRow(), botMove.getCol());

            EngineGameStateResponseDTO botGameOver = moveProcessorService.checkGameOver(
                    board, botSymbol, BotConstants.BOT_USER_ID, players, getBotDifficultyFromGameState(gameState)
            );

            if (botGameOver != null) {
                moveProcessorService.persistUpdatedState(
                        realtimeState,
                        botGameOver
                );

                return botGameOver;
            }
            PlayerDTO humanPlayer = getHumanPlayer(players);

            EngineGameStateResponseDTO response = moveProcessorService.buildResponse(
                            board,
                            humanPlayer.getUserId(),
                            GameStatusEnum.RUNNING,
                            null,
                            players,
                            getBotDifficultyFromGameState(gameState));

            moveProcessorService.persistUpdatedState(realtimeState, response);
            return response;
        }

        validateTurn(realtimeState.getCurrentTurnUserId(),
                request.getUserId()
        );
        Move move = MoveMapper.toDomain(request.getMoveData());
        PlayerDTO currentPlayer = getPlayerByUserId(
                players,
                request.getUserId()
        );

        String playerSide = currentPlayer.getSide();
        CellValue currentPlayerSymbol = CellValue.valueOf(playerSide);

        moveProcessorService.validateMove(board, move);
            moveProcessorService.applyMove(board, move, currentPlayerSymbol);

        log.info(
                "[MAKE_MOVE] Move applied: [{}] at [{}, {}]",
                playerSide,
                move.getRow(),
                move.getCol()
        );

        EngineGameStateResponseDTO gameOverResponse = moveProcessorService.checkGameOver(
                        board,
                        currentPlayerSymbol,
                        request.getUserId(),
                        players,
                        getBotDifficultyFromGameState(gameState)
                );
        if (gameOverResponse != null) {

            moveProcessorService.persistUpdatedState(
                    realtimeState,
                    gameOverResponse
            );
            return gameOverResponse;
        }

        PlayerDTO nextPlayer = moveProcessorService.getNextPlayer(players, request.getUserId());
        EngineGameStateResponseDTO response = moveProcessorService.buildResponse(
                board,
                nextPlayer.getUserId(),
                GameStatusEnum.RUNNING,
                null,
                players,
                getBotDifficultyFromGameState(gameState)
        );
            log.info("Response players after build = {}", response.getPlayers());
            moveProcessorService.persistUpdatedState(realtimeState, response);

            if (Boolean.TRUE.equals(nextPlayer.getIsBot())) {

                botMoveService.processBotMove(
                        request.getRoomId()
                );

                log.info(
                        "[BOT_SCHEDULED] roomId={}",
                        request.getRoomId()
                );
            }


        return response;
    }

    private List<PlayerDTO> buildPlayers(EngineStartGameRequestDTO request) {
        List<PlayerDTO> players = new ArrayList<>();
        // pvp
                if (request.getMatchType() == MatchTypeEnum.PVP) {
                    players.add(buildPlayer(
                            request.getPlayerIds().get(0),
                            1,
                            "X",
                            false,
                            "PLAYER_1"
                    ));

                    players.add(buildPlayer(
                            request.getPlayerIds().get(1),
                            2,
                            "O",
                            false,
                            "PLAYER_2"
                    ));
                }

        // bot
        else {
            players.add(buildPlayer(
                            request.getPlayerIds().get(0),
                            1,
                            "X",
                            false,
                    "PLAYER"
                    ));

            players.add(buildPlayer(
                            BotConstants.BOT_USER_ID,
                            2,
                            "O",
                            true,
                    "BOT"
                    ));
        }
        return players;
    }

    private PlayerDTO buildPlayer(Long userId,
                                  Integer turnOrder,
                                  String side,
                                  Boolean isBot,
                                  String displayName)
    {
        return PlayerDTO.builder()
                .userId(userId)
                .displayName(displayName)
                .turnOrder(turnOrder)
                .side(side)
                .isBot(isBot)
                    .build();
    }

    private void validateTurn(
            Long currentTurnUserId,
            Long userId
    ) {

        if (!userId.equals(currentTurnUserId)) {
            throw new TicTacToeEngineException(
                    INVALID_TURN
            );
        }
    }







    private PlayerDTO getPlayerByUserId(List<PlayerDTO> players, Long userId) {
        return players.stream()
                .filter(player -> player.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new TicTacToeEngineException(PLAYER_NOT_FOUND));
    }



    private PlayerDTO getHumanPlayer(List<PlayerDTO> players) {
        return players.stream()
                .filter(player ->
                        !Boolean.TRUE.equals(player.getIsBot())
                )
                .findFirst()
                .orElseThrow(() ->
                        new TicTacToeEngineException(
                                PLAYER_NOT_FOUND
                        )
                );
    }

    private List<PlayerDTO> extractPlayersFromGameState(
            Map<String, Object> gameState) {

        Object playersObject = gameState.get("players");
        return objectMapper.convertValue(playersObject,
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, PlayerDTO.class));
    }
    private BotDifficultyEnum getBotDifficultyFromGameState(
            Map<String, Object> gameState
    ) {

        Object difficulty =
                gameState.get("botDifficulty");

        if (difficulty == null) {
            return null;
        }

        return BotDifficultyEnum.valueOf(
                difficulty.toString()
        );
    }
}