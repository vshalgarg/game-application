package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.bot.factory.BotFactory;
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

    private final BotFactory botFactory;

    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDto request) {

        log.info("Initializing new game for room: {}", request.getRoomCode());
        Board board = new Board();
        log.debug("[START_GAME] 3x3 board initialized successfully");

        // Assign Players
        List<PlayerDto> players = new ArrayList<>();
        players.add(
                new PlayerDto(
                        request.getPlayerIds().get(0),
                        1,
                        "X"
                )
        );

        players.add(
                new PlayerDto(
                        request.getPlayerIds().get(1),
                        2,
                        "O"
                )
        );

        log.info(
                "[START_GAME] Players assigned - P1: {}, P2: {}",
                request.getPlayerIds().get(0),
                request.getPlayerIds().get(1)
        );

        return buildResponse(
                board,
                players.get(0).getUserId(),
                GameStatusEnum.INITIALIZED,
                null,
                players
        );
    }

    @Override
    public EngineGameStateResponseDTO makeMove(EngineMoveRequestDTO request) {

        log.info(
                "[MAKE_MOVE] Processing move for user: {} in room: {}",
                request.getUserId(),
                request.getRoomId()
        );

        // DTO → Domain Mapping
        Board board = BoardMapper.toDomain(request.getBoardState());
        Move move = MoveMapper.toDomain(request.getMoveData());
        List<PlayerDto> players = request.getPlayers();

        // Validate Turn
        if (!request.getUserId().equals(request.getCurrentTurnUserId())) {
            throw new TicTacToeEngineException(INVALID_TURN);
        }

        // Find Current Player
        PlayerDto currentPlayer = players.stream()
                .filter(player -> player.getUserId().equals(request.getUserId()))
                .findFirst()
                .orElseThrow(() ->
                        new TicTacToeEngineException(PLAYER_NOT_FOUND)
                );

        String playerSide = currentPlayer.getSide();
        CellValue symbol = CellValue.valueOf(playerSide);

        // Validate Move
        if (move.getRow() < 0 || move.getRow() > 2 ||
                move.getCol() < 0 || move.getCol() > 2 ||
                !board.isCellEmpty(move.getRow(), move.getCol())) {
            throw new TicTacToeEngineException(INVALID_MOVE);
        }

        // Apply Move
        board.setCell(
                move.getRow(),
                move.getCol(),
                symbol
        );

        log.info(
                "[MAKE_MOVE] Move applied: [{}] at [{}, {}]",
                playerSide,
                move.getRow(),
                move.getCol()
        );

        // Check Winner
        if (board.checkWin(symbol)) {

            log.info(
                    "[GAME_OVER] Player {} won the game!",
                    request.getUserId()
            );

            return buildResponse(
                    board,
                    null,
                    GameStatusEnum.WIN,
                    request.getUserId(),
                    players
            );
        }

        // Check Draw
        if (board.isBoardFull()) {

            log.info("[GAME_OVER] Match ended in DRAW");

            return buildResponse(
                    board,
                    null,
                    GameStatusEnum.DRAW,
                    null,
                    players
            );
        }

        // Switch Turn
        Long nextTurnUserId = players.stream()
                .filter(player ->
                        !player.getUserId().equals(request.getUserId())
                )
                .map(PlayerDto::getUserId)
                .findFirst()
                .orElse(null);

        log.info(
                "[MAKE_MOVE] Move successful. Next turn: {}",
                nextTurnUserId
        );

        return buildResponse(
                board,
                nextTurnUserId,
                GameStatusEnum.RUNNING,
                null,
                players
        );
    }

    private EngineGameStateResponseDTO buildResponse(
            Board board,
            Long currentTurnUserId,
            GameStatusEnum status,
            Long winnerUserId,
            List<PlayerDto> players
    ) {

        log.info(
                "[BUILD_RESPONSE] Status: {}, Next Turn: {}, Winner: {}",
                status,
                currentTurnUserId,
                winnerUserId
        );

        return EngineGameStateResponseDTO.builder()
                .boardState(BoardMapper.toDto(board))
                .currentTurnUserId(currentTurnUserId)
                .status(status)
                .winnerUserId(winnerUserId)
                .players(players)
                .build();
    }
}