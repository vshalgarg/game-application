package com.codemonks.ludo_engine.service.Impl;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.common.TokenDTO;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.BoardService;
import com.codemonks.ludo_engine.service.PathOrderService;
import com.codemonks.ludo_engine.service.TokenMovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.codemonks.ludo_engine.constant.LudoErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenMovementServiceImpl implements TokenMovementService {

    private final BoardService boardService;
    private final PathOrderService pathOrderService;

    @Override
    public GameStateDTO moveToken(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId,
            Integer consumedDice
    ) {

        log.info(
                "[TOKEN_MOVEMENT_STARTED] Player:{} Token:{} Dice:{}",
                playerId,
                tokenId,
                consumedDice
        );

        for (PlayerDTO player : gameState.getPlayers()) {

            if (!player.getPlayerId().equals(playerId)) {
                continue;
            }
            if (player.getPendingDice() == null
                    || !player.getPendingDice().contains(consumedDice)) {

                throw new InvalidMoveException(
                        INVALID_MOVE
                );
            }

            log.info(
                    "[MOVE_START] Player:{} Token:{} Dice:{}",
                    playerId,
                    tokenId,
                    consumedDice
            );

            for (TokenDTO token : player.getTokens()) {

                if (!token.getTokenId().equals(tokenId)) {
                    continue;
                }

                // BASE -> TRACK
                // BASE -> TRACK
                if (token.getState() == TokenStateEnum.BASE) {

                    if (consumedDice != 6) {
                        throw new InvalidMoveException(INVALID_MOVE);
                    }

                    token.setState(TokenStateEnum.TRACK);
                    token.setPathIndex(0);

                    consumePendingDiceByIndex(player, consumedDice);

                    log.info(
                            "[BASE_TO_TRACK] Player:{} Token:{} PathIndex:{}",
                            playerId,
                            tokenId,
                            token.getPathIndex()
                    );

                    return gameState;
                }
                // TRACK movement
                if (token.getState() == TokenStateEnum.TRACK) {

                    if (token.getPathIndex() == null) {
                        throw new InvalidMoveException(INVALID_MOVE);
                    }

                    Integer colorIndex =
                            pathOrderService.getPathOrder(
                                    gameState,
                                    player.getPlayerId()
                            );

                    List<Integer> path =
                            boardService.getPath(colorIndex);
                    log.info(
                            "[TRACK_MOVE_STARTED] Player:{} Token:{} CurrentPathIndex:{} Dice:{}",
                            playerId,
                            tokenId,
                            token.getPathIndex(),
                            consumedDice
                    );

                    if (path == null || path.isEmpty()) {
                        throw new InvalidMoveException(INVALID_MOVE);
                    }
                    int currentPathIndex = token.getPathIndex();
                    int newPathIndex = currentPathIndex + consumedDice;

                    // Exact dice required to finish
                    if (newPathIndex > path.size() - 1) {
                        throw new InvalidMoveException(INVALID_MOVE);
                    }

                    // Token reached goal
                    if (newPathIndex == path.size() - 1) {
                        token.setPathIndex(newPathIndex);
                        token.setState(TokenStateEnum.FINISHED);
                        consumePendingDiceByIndex(player, consumedDice);

                        log.info(
                                "[TOKEN_FINISHED] Player:{} Token:{} PathIndex:{}",
                                playerId,
                                tokenId,
                                newPathIndex
                        );

                        return gameState;
                    }

                    // Normal movement
                    token.setPathIndex(newPathIndex);

                    consumePendingDiceByIndex(player, consumedDice);

                    log.info(
                            "[TRACK_MOVED] Player:{} Token:{} FromPathIndex:{} ToPathIndex:{}",
                            playerId,
                            tokenId,
                            currentPathIndex,
                            newPathIndex
                    );

                    return gameState;
                }

                if (token.getState() == TokenStateEnum.FINISHED) {

                    throw new InvalidMoveException(
                            INVALID_MOVE
                    );
                }
            }
        }
        log.error(
                "[TOKEN_MOVEMENT_FAILED] Player:{} Token:{} Dice:{}",
                playerId,
                tokenId,
                consumedDice
        );
        throw new InvalidMoveException(
                INVALID_MOVE
        );
    }

    private void consumePendingDiceByIndex(
            PlayerDTO player,
            Integer consumedDice
    ) {

        boolean removed =
                player.getPendingDice()
                        .remove(consumedDice);

        log.info(
                "[BUFFER_UPDATED] RemovedDice:{} Success:{} Remaining:{}",
                consumedDice,
                removed,
                player.getPendingDice()
        );
    }

}