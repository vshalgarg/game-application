package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.ludo_engine.enums.GameStatusEnum;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.BoardService;
import com.codemonks.ludo_engine.service.WinConditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.codemonks.ludo_engine.constant.LudoErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class WinConditionServiceImpl implements WinConditionService {

    private final BoardService boardService;

    @Override
    public EngineGameStateResponseDTO checkWinner(GameStateDTO gameState, Long playerId) {

        log.info("[WINNER_CHECK_STARTED] Player:{}", playerId);

        EngineGameStateResponseDTO response = new EngineGameStateResponseDTO();

        // Default: game continues unless we prove otherwise below
        response.setStatus(gameState.getGameStatus());

        PlayerDTO currentPlayer = gameState.getPlayers().stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("[PLAYER_NOT_FOUND] Player:{} not found in gameState", playerId);
                    return new InvalidMoveException(INVALID_MOVE);
                });

        int tokensRequiredToWin = boardService.getBoard().getMetadata().getTokensPerPlayer();

        long finishedTokenCount = currentPlayer.getTokens().stream()
                .filter(token -> token.getState() == TokenStateEnum.FINISHED)
                .count();

        log.info(
                "[FINISHED_TOKENS_CHECK] Player:{} Finished:{}/{}",
                playerId,
                finishedTokenCount,
                tokensRequiredToWin
        );

        if (finishedTokenCount >= tokensRequiredToWin) {

            currentPlayer.setHasFinished(true);

            if (gameState.getFinishOrder() == null) {
                gameState.setFinishOrder(new java.util.ArrayList<>());
            }

            if (!gameState.getFinishOrder().contains(playerId)) {
                gameState.getFinishOrder().add(playerId);
                log.info(
                        "[PLAYER_FINISHED] Player:{} Rank:{}",
                        playerId,
                        gameState.getFinishOrder().size()
                );
            }

            int totalPlayers = gameState.getPlayers().size();
            int finishedPlayers = gameState.getFinishOrder().size();

            if (finishedPlayers >= totalPlayers - 1) {

                gameState.getPlayers().stream()
                        .filter(p -> !gameState.getFinishOrder().contains(p.getPlayerId()))
                        .findFirst()
                        .ifPresent(lastPlayer -> {
                            gameState.getFinishOrder().add(lastPlayer.getPlayerId());
                            lastPlayer.setHasFinished(true);
                            log.info(
                                    "[LAST_PLAYER_RANKED] Player:{} Rank:{}",
                                    lastPlayer.getPlayerId(),
                                    gameState.getFinishOrder().size()
                            );
                        });

                response.setWinnerUserId(gameState.getFinishOrder().get(0));
                response.setStatus(GameStatusEnum.FINISHED);

                gameState.setWinnerPlayerId(gameState.getFinishOrder().get(0));
                gameState.setGameStatus(GameStatusEnum.FINISHED);

                log.info(
                        "[GAME_FINISHED] FinishOrder:{}",
                        gameState.getFinishOrder()
                );

            } else {

                log.info(
                        "[PLAYER_FINISHED_GAME_CONTINUES] Player:{} RemainingActivePlayers:{}",
                        playerId,
                        totalPlayers - finishedPlayers
                );
            }
        }
        else {
            log.info(
                    "[WINNER_CHECK_PENDING] Player:{} Finished:{}/{}",
                    playerId,
                    finishedTokenCount,
                    tokensRequiredToWin
            );
        }
        log.info(
                "[WINNER_CHECK_COMPLETED] Player:{} Winner:{}",
                playerId,
                response.getWinnerUserId() != null
        );
        return response;
    }
}