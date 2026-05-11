package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineStartGameRequestDto;
import com.codemonks.tic_tac_toe_game_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import com.codemonks.tic_tac_toe_game_engine.exception.TicTacToeEngineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.codemonks.tic_tac_toe_game_engine.constant.EngineErrorCodesEnum.INVALID_MOVE;
import static com.codemonks.tic_tac_toe_game_engine.constant.EngineErrorCodesEnum.INVALID_TURN;

@Service
@Slf4j
public class TicTacToeEngineImpl implements TicTacToeEngine {

    @Override
    public EngineGameStateResponseDTO startGame(EngineStartGameRequestDto request) {
        log.info("Initializing new game for room: {}", request.getRoomCode());

        // 1. Create 3x3 empty board
        List<List<String>> board = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            board.add(new ArrayList<>(Collections.nCopies(3, null)));
        }log.debug("[START_GAME] 3x3 empty board created successfully");


        // 2. Assign Players (Player 1 = X, Player 2 = O)
        List<PlayerDto> players = new ArrayList<>();
        players.add(new PlayerDto(request.getPlayerIds().get(0), 1, "X"));
        players.add(new PlayerDto(request.getPlayerIds().get(1), 2, "O"));
        log.info("[START_GAME] Players assigned - P1: {}, P2: {}", request.getPlayerIds().get(0), request.getPlayerIds().get(1));

        return buildResponse(board, players.get(0).getUserId(), GameStatusEnum.INITIALIZED, null, players);
    }

    @Override
    public EngineGameStateResponseDTO makeMove(EngineMoveRequestDTO request) {
        log.info("[MAKE_MOVE] Processing move for user: {} in game: {}", request.getUserId(), request.getGameId());

        List<List<String>> board = request.getBoardState();
        List<PlayerDto> players = request.getPlayers();

        if (!request.getUserId().equals(request.getCurrentTurnUserId())) {
            throw new TicTacToeEngineException(INVALID_TURN);
        }
        // 1. Find Current Player side (X or O)
        PlayerDto currentPlayer = players.stream()
                .filter(p -> p.getUserId().equals(request.getUserId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found in game!"));

        String playerSign = currentPlayer.getSide();

        // 2. Extract and Validate Move Coordinates
        int row = (int) request.getMoveData().get("row");
        int col = (int) request.getMoveData().get("col");

        if (row < 0 || row > 2 || col < 0 || col > 2 || board.get(row).get(col) != null) {
            throw new TicTacToeEngineException(INVALID_MOVE);
        }

        // 3. Apply Move
        board.get(row).set(col, playerSign);
        log.info("[MAKE_MOVE] Move applied: [{}] at [{}, {}]", playerSign, row, col);

        // 4. Check for Win
        if (checkWin(board, playerSign)) {
            log.info("[GAME_OVER] Player {} (Side: {}) won the game!", request.getUserId());
            return buildResponse(board, null, GameStatusEnum.WIN, request.getUserId(), players);
        }

        // 5. Check for Draw
        if (isBoardFull(board)) {
            log.info("[GAME_OVER] Game ended in a DRAW - Board is full");
            return buildResponse(board, null, GameStatusEnum.DRAW, null, players);
        }

        // 6. Switch Turn to the OTHER player
        Long nextTurnUserId = players.stream()
                .filter(p -> !p.getUserId().equals(request.getUserId()))
                .map(PlayerDto::getUserId)
                .findFirst()
                .orElse(null);
        log.info("[MAKE_MOVE] Move successful. Next turn: {}", nextTurnUserId);
        return buildResponse(board, nextTurnUserId, GameStatusEnum.RUNNING, null, players);
    }

    private boolean checkWin(List<List<String>> board, String s) {
        // Rows & Columns
        for (int i = 0; i < 3; i++) {
            if (Objects.equals(board.get(i).get(0), s) && Objects.equals(board.get(i).get(1), s) && Objects.equals(board.get(i).get(2), s)) return true;
            if (Objects.equals(board.get(0).get(i), s) && Objects.equals(board.get(1).get(i), s) && Objects.equals(board.get(2).get(i), s)) return true;
        }
        // Diagonals
        if (Objects.equals(board.get(0).get(0), s) && Objects.equals(board.get(1).get(1), s) && Objects.equals(board.get(2).get(2), s)) return true;
        return Objects.equals(board.get(0).get(2), s) && Objects.equals(board.get(1).get(1), s) && Objects.equals(board.get(2).get(0), s);
    }

    private boolean isBoardFull(List<List<String>> board) {

        boolean full = board.stream()
                .flatMap(List::stream)
                .noneMatch(Objects::isNull);

        if (full) {
            log.info("[CHECK_BOARD] Board is completely full. No more moves possible.");
        } else {
            log.debug("[CHECK_BOARD] Board still has empty cells.");
        }
        return full;
    }

    private EngineGameStateResponseDTO buildResponse(List<List<String>> board, Long turnId, GameStatusEnum status, Long winnerId, List<PlayerDto> players) {
        log.info("[BUILD_RESPONSE] Finalizing state - Status: {}, Next Turn: {}, Winner: {}",
                status, turnId, winnerId);
        return EngineGameStateResponseDTO.builder()
                .boardState(board)
                .currentTurnUserId(turnId)
                .status(status)
                .winnerUserId(winnerId)
                .players(players)
                .build();
    }
}