package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.constant.BoardConstants;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.common.TokenDTO;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.TokenMovementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.codemonks.ludo_engine.constant.ErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
public class TokenMovementServiceImpl implements TokenMovementService {

    @Override
    public GameStateDTO moveToken(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId,
            Integer consumedDice
    ) {

        log.info(
                "[TOKEN_MOVEMENT_STARTED] Player:{} Token:{} DiceIndex:{}",
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
                if (token.getState() == TokenStateEnum.BASE) {

                    int trackStart =
                            BoardConstants.TRACK_START.get(token.getColor());

                    token.setState(TokenStateEnum.TRACK);
                    token.setPosition(trackStart);

                    consumePendingDiceByIndex(
                            player,
                            consumedDice
                    );

                    log.info(
                            "[BASE_TO_TRACK] Player:{} Token:{} Color:{} Position:{}",
                            playerId,
                            tokenId,
                            token.getColor(),
                            trackStart
                    );

                    return gameState;
                }

                // HOME_PATH movement
                if (token.getState() == TokenStateEnum.HOME_PATH) {

                    int currentPos = token.getPosition();
                    int newPos = currentPos + consumedDice;

                    if (newPos > BoardConstants.HOME_PATH_SIZE - 1) {

                        throw new InvalidMoveException(
                                INVALID_MOVE
                        );
                    }

                    token.setPosition(newPos);

                    consumePendingDiceByIndex(
                            player,
                            consumedDice
                    );

                    log.info(
                            "[HOME_PATH_MOVED] Player:{} Token:{} From:{} To:{}",
                            playerId,
                            tokenId,
                            currentPos,
                            newPos
                    );

                    return gameState;
                }

                // TRACK movement
                if (token.getState() == TokenStateEnum.TRACK) {

                    int currentPosition = token.getPosition();

                    int trackStart =
                            BoardConstants.TRACK_START.get(token.getColor());

                    int effectiveDistance =
                            (currentPosition
                                    - trackStart
                                    + BoardConstants.BOARD_SIZE)
                                    % BoardConstants.BOARD_SIZE;

                    int newEffectiveDistance =
                            effectiveDistance + consumedDice;

                    if (newEffectiveDistance
                            >= BoardConstants.HOME_PATH_ENTRY_DISTANCE) {

                        int homePathPosition =
                                newEffectiveDistance
                                        - BoardConstants.HOME_PATH_ENTRY_DISTANCE;

                        token.setState(TokenStateEnum.HOME_PATH);
                        token.setPosition(homePathPosition);

                        consumePendingDiceByIndex(
                                player,
                                consumedDice
                        );

                        log.info(
                                "[TRACK_TO_HOME_PATH] Player:{} Token:{} HomePos:{}",
                                playerId,
                                tokenId,
                                homePathPosition
                        );

                        return gameState;
                    }

                    int newPosition =
                            (currentPosition + consumedDice)
                                    % BoardConstants.BOARD_SIZE;

                    token.setPosition(newPosition);

                    consumePendingDiceByIndex(
                            player,
                            consumedDice
                    );

                    log.info(
                            "[TRACK_MOVED] Player:{} Token:{} From:{} To:{}",
                            playerId,
                            tokenId,
                            currentPosition,
                            newPosition
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