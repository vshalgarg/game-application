package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.bot.enums.BotDifficultyEnum;
import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import com.codemonks.tic_tac_toe_game_engine.exception.TicTacToeEngineException;
import com.codemonks.tic_tac_toe_game_engine.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codemonks.tic_tac_toe_game_engine.constant.EngineErrorCodesEnum.INVALID_MOVE;
import static com.codemonks.tic_tac_toe_game_engine.constant.EngineErrorCodesEnum.PLAYER_NOT_FOUND;
@Slf4j
@Service
@RequiredArgsConstructor
public class MoveProcessorService {

    private final SupabaseRealtimeService supabaseRealtimeService;


    public void validateMove(Board board, Move move) {
        if (move.getRow() < 0 || move.getRow() > 2 || move.getCol() < 0 || move.getCol() > 2 || !board.isCellEmpty(move.getRow(), move.getCol())) {
            throw new TicTacToeEngineException(INVALID_MOVE);
        }
    }

    public void applyMove(Board board, Move move, CellValue symbol) {
        board.setCell(move.getRow(), move.getCol(), symbol);}


    public EngineGameStateResponseDTO checkGameOver(
            Board board,
            CellValue symbol,
            Long winnerUserId,
            List<PlayerDTO> players,
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
                    difficulty,
                    winnerUserId
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
                    difficulty,
                    winnerUserId
            );
        }
        return null;
    }


    public PlayerDTO getNextPlayer(List<PlayerDTO> players, Long currentUserId) {
        return players.stream()
                .filter(player -> !player.getUserId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new TicTacToeEngineException(PLAYER_NOT_FOUND));
    }

    public EngineGameStateResponseDTO buildResponse(
            Board board,
            Long currentTurnUserId,
            GameStatusEnum status,
            Long winnerUserId,
            List<PlayerDTO> players,
            BotDifficultyEnum botDifficulty,
            Long lastMoverUserId) {

        log.info(
                "[BUILD_RESPONSE] Status: {}, Next Turn: {}, Winner: {}",
                status,
                currentTurnUserId,
                winnerUserId
        );

        log.info("Response players before build = {}", players);

        return EngineGameStateResponseDTO.builder()
                .gameState(buildGameState(board,players,botDifficulty, lastMoverUserId))
                .currentTurnUserId(currentTurnUserId)
                .status(status)
                .winnerUserId(winnerUserId)
                .players(players)
                .botDifficulty(botDifficulty)
                .build();
    }

    public Map<String, Object> buildGameState(Board board,
                                              List<PlayerDTO> players,
                                              BotDifficultyEnum botDifficulty,Long lastMoverUserId) {

        Map<String, Object> gameState =
                new HashMap<>(BoardMapper.toMap(board));
        gameState.put("players", players);

        if (botDifficulty != null) {
            gameState.put("botDifficulty", botDifficulty.name());
        }

        if (lastMoverUserId != null) {
            gameState.put("lastMoverUserId", lastMoverUserId);
        }

        return gameState;
    }

    public void persistUpdatedState(
            RealtimeGameStateDTO existingState,
            EngineGameStateResponseDTO response) {

        RealtimeGameStateDTO updatedState =
                RealtimeGameStateDTO.builder()
                        .roomId(existingState.getRoomId())
                        .roomCode(existingState.getRoomCode())
                        .gameState(response.getGameState())
                        .currentTurnUserId(response.getCurrentTurnUserId())
                        .gameStatus(response.getStatus().name())
                        .winnerUserId(response.getWinnerUserId())
                        .players(response.getPlayers())
                        .botDifficulty(response.getBotDifficulty())
                        .stateSequence(existingState.getStateSequence() + 1)
                        .build();
        supabaseRealtimeService.upsertGameState(updatedState);
    }

}
