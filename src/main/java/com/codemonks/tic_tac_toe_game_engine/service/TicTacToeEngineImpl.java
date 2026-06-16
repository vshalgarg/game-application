package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.bot.constants.BotConstants;
import com.codemonks.tic_tac_toe_game_engine.bot.enums.BotDifficultyEnum;
import com.codemonks.tic_tac_toe_game_engine.bot.enums.MatchTypeEnum;
import com.codemonks.tic_tac_toe_game_engine.bot.factory.BotStrategyFactory;
import com.codemonks.tic_tac_toe_game_engine.bot.strategy.BotStrategy;
import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineStartGameRequestDto;
import com.codemonks.tic_tac_toe_game_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import com.codemonks.tic_tac_toe_game_engine.exception.TicTacToeEngineException;
import com.codemonks.tic_tac_toe_game_engine.mapper.BoardMapper;
import com.codemonks.tic_tac_toe_game_engine.mapper.MoveMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.codemonks.tic_tac_toe_game_engine.constant.EngineErrorCodesEnum.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicTacToeEngineImpl implements TicTacToeEngine {

    private final BotStrategyFactory botFactory;

    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDto request) {

        log.info("Initializing new game for room: {}", request.getRoomCode());
        Board board = new Board();
        log.debug("[START_GAME] 3x3 board initialized successfully");

        // Assign Players
        List<PlayerDto> players = buildPlayers(request);

        log.info(
                "[START_GAME] Match initialized | Room: {} | MatchType: {} | Players: {} vs {}",
                request.getRoomCode(),
                request.getMatchType(),
                players.get(0).getUserId(),
                players.get(1).getUserId()
        );

        EngineGameStateResponseDTO response = buildResponse(
                board,
                players.get(0).getUserId(),
                GameStatusEnum.INITIALIZED,
                null,
                players,
                request.getBotDifficulty()
        );
        return response;
    }

    @Override
    public EngineGameStateResponseDTO makeMove(EngineMoveRequestDTO request) {

        log.info(
                "[MAKE_MOVE] Processing move for user: {} in room: {}",
                request.getUserId(),
                request.getRoomId()
        );

        // DTO → Domain Mapping
        Board board = BoardMapper.toDomain(request.getGameState());
        Move move = MoveMapper.toDomain(request.getMoveData());
        List<PlayerDto> players = normalizePlayers(request);
        request.setPlayers(players);
        validateTurn(request);

        PlayerDto currentPlayer = getPlayerByUserId(
                players,
                request.getUserId()
        );

        String playerSide = currentPlayer.getSide();
        CellValue currentPlayerSymbol = CellValue.valueOf(playerSide);

        validateMove(board, move);
        applyMove(board, move, currentPlayerSymbol);

        log.info(
                "[MAKE_MOVE] Move applied: [{}] at [{}, {}]",
                playerSide,
                move.getRow(),
                move.getCol()
        );

        EngineGameStateResponseDTO gameOverResponse =
                checkGameOver(
                        board,
                        currentPlayerSymbol,
                        request.getUserId(),
                        players,
                        request.getBotDifficulty()
                );

        if (gameOverResponse != null) {
            return gameOverResponse;
        }

        PlayerDto nextPlayer = getNextPlayer(
                players,
                request.getUserId()
        );

        if (Boolean.TRUE.equals(nextPlayer.getIsBot())) {
            processBotMove(board, nextPlayer, request.getBotDifficulty());
            CellValue botSymbol = CellValue.valueOf(nextPlayer.getSide());
            EngineGameStateResponseDTO botGameOverResponse = checkGameOver(
                            board,
                            botSymbol,
                            nextPlayer.getUserId(),
                            players,
                            request.getBotDifficulty()
                    );

            if (botGameOverResponse != null) return botGameOverResponse;

            Long humanUserId = getHumanPlayer(players).getUserId();

            return buildResponse(
                    board,
                    humanUserId,
                    GameStatusEnum.RUNNING,
                    null,
                    players,
                    request.getBotDifficulty()
            );
        }

        return buildResponse(
                board,
                nextPlayer.getUserId(),
                GameStatusEnum.RUNNING,
                null,
                players,
                request.getBotDifficulty()
        );
    }

    private List<PlayerDto> buildPlayers(
            EngineStartGameRequestDto request
    ) {
        List<PlayerDto> players = new ArrayList<>();

        // pvp
        if (request.getMatchType() == MatchTypeEnum.PVP) {
            players.add(buildPlayer(
                            request.getPlayerIds().get(0),
                            1,
                            "X",
                            false
                    )
            );

            players.add(buildPlayer(
                            request.getPlayerIds().get(1),
                            2,
                            "O",
                            false
                    )
            );
        }

        // bot
        else {
            players.add(buildPlayer(
                            request.getPlayerIds().get(0),
                            1,
                            "X",
                            false
                    )
            );

            players.add(buildPlayer(
                            BotConstants.BOT_USER_ID,
                            2,
                            "O",
                            true
                    )
            );
        }
        return players;
    }

    private PlayerDto buildPlayer(Long userId, Integer turnOrder, String side, Boolean isBot) {
        return PlayerDto.builder()
                .userId(userId)
                .turnOrder(turnOrder)
                .side(side)
                .isBot(isBot)
                    .build();
    }

    private void validateTurn(
            EngineMoveRequestDTO request
    ) {
        if (!request.getUserId().equals(request.getCurrentTurnUserId())) {
            throw new TicTacToeEngineException(INVALID_TURN);
        }
    }

    private void validateMove(Board board, Move move) {
        if (move.getRow() < 0 || move.getRow() > 2 || move.getCol() < 0 || move.getCol() > 2 || !board.isCellEmpty(move.getRow(), move.getCol())) {
            throw new TicTacToeEngineException(INVALID_MOVE);
        }
    }

    private void applyMove(Board board, Move move, CellValue symbol) {
        board.setCell(move.getRow(), move.getCol(), symbol);
    }

    private void processBotMove(
            Board board,
            PlayerDto botPlayer,
            BotDifficultyEnum difficulty
    ) {

        try {
            Thread.sleep(getBotDelay(difficulty));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[BOT_MOVE] Bot thinking delay was interrupted");}

        BotStrategy strategy = botFactory.getStrategy(difficulty);
        CellValue botSymbol = CellValue.valueOf(botPlayer.getSide());
        Move botMove = strategy.chooseMove(board, botSymbol);
        applyMove(board, botMove, botSymbol);
        log.info(
                "[BOT_MOVE] {} bot played at [{},{}]",
                difficulty,
                botMove.getRow(),
                botMove.getCol()
        );
    }

    private int getBotDelay(BotDifficultyEnum difficulty) {
        return switch (difficulty) {
            case EASY   -> 600;
            case MEDIUM -> 1000;
            case HARD   -> 1500;
        };
    }




    private EngineGameStateResponseDTO checkGameOver(
            Board board,
            CellValue symbol,
            Long winnerUserId,
            List<PlayerDto> players,
            BotDifficultyEnum difficulty
    ) {

        // win
        if (board.checkWin(symbol)) {
            log.info("[GAME_OVER] Winner: {}", winnerUserId);
            return buildResponse(
                    board,
                    null,
                    GameStatusEnum.WIN,
                    winnerUserId,
                    players,
                    difficulty
            );
        }

        // draw
        if (board.isBoardFull()) {
            log.info("[GAME_OVER] Match DRAW");
            return buildResponse(
                    board,
                    null,
                    GameStatusEnum.DRAW,
                    null,
                    players,
                    difficulty
            );
        }
        return null;
    }

    private PlayerDto getPlayerByUserId(List<PlayerDto> players, Long userId) {
        return players.stream()
                .filter(player -> player.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new TicTacToeEngineException(PLAYER_NOT_FOUND));
    }

    private PlayerDto getNextPlayer(List<PlayerDto> players, Long currentUserId) {
        return players.stream()
                .filter(player -> !player.getUserId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new TicTacToeEngineException(PLAYER_NOT_FOUND));
    }

    private PlayerDto getHumanPlayer(List<PlayerDto> players) {
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

    private EngineGameStateResponseDTO buildResponse(
            Board board,
            Long currentTurnUserId,
            GameStatusEnum status,
            Long winnerUserId,
            List<PlayerDto> players,
            BotDifficultyEnum botDifficulty
    ) {

        log.info(
                "[BUILD_RESPONSE] Status: {}, Next Turn: {}, Winner: {}",
                status,
                currentTurnUserId,
                winnerUserId
        );

        return EngineGameStateResponseDTO.builder()
                .gameState(BoardMapper.toMap(board))
                .currentTurnUserId(currentTurnUserId)
                .status(status)
                .winnerUserId(winnerUserId)
                .players(players)
                .botDifficulty(botDifficulty)
                .build();
    }

    private List<PlayerDto> normalizePlayers(
            EngineMoveRequestDTO request
    ){
        List<PlayerDto> players = new ArrayList<>(request.getPlayers());
        if (request.getBotDifficulty() != null && players.size() == 1) {
            players.add(buildPlayer(BotConstants.BOT_USER_ID, 2, "O", true));
        }
        return players;
    }
}