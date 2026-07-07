package com.codemonks.ludo_game_engine.service.Impl;

import com.codemonks.ludo_game_engine.constant.BoardConstants;
import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_game_engine.dto.common.TokenDTO;
import com.codemonks.ludo_game_engine.enums.TokenStateEnum;
import com.codemonks.ludo_game_engine.exception.InvalidMoveException;
import com.codemonks.ludo_game_engine.service.MoveValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MoveValidationServiceImpl implements MoveValidationService {

    @Override
    public void validateMove(
            GameStateDTO gameState,
            Long userId,
            Long tokenId,
            Integer consumedDice
    ) {

        log.info(
                "[MOVE_VALIDATION_STARTED] Player:{} Token:{} Dice:{}",
                userId,
                tokenId,
                consumedDice
        );

        validateTokenExists(gameState, userId, tokenId);
        validateConsumedDice(gameState, userId, consumedDice);
        validateBaseExitRule(gameState, userId, tokenId, consumedDice);
        validateHomePathOvershoot(gameState, userId, tokenId, consumedDice);

        log.info(
                "[MOVE_VALIDATION_COMPLETED] Player:{} Token:{} Dice:{}",
                userId,
                tokenId,
                consumedDice
        );
    }

    private void validateTokenExists(
            GameStateDTO gameState,
            Long userId,
            Long tokenId
    ) {

        for (PlayerDTO player : gameState.getPlayers()) {

            if (!player.getPlayerId().equals(userId)) {
                continue;
            }

            for (TokenDTO token : player.getTokens()) {

                if (token.getTokenId().equals(tokenId)) {
                    log.info("[TOKEN_VALIDATED] Player:{} Token:{}",
                            userId,
                            tokenId);
                    return;
                }
            }
        }

        log.error(
                "[TOKEN_NOT_FOUND] Player:{} Token:{}",
                userId,
                tokenId
        );

        throw new InvalidMoveException("Token not found");
    }

    private void validateConsumedDice(
            GameStateDTO gameState,
            Long userId,
            Integer consumedDice
    ) {

        if (consumedDice == null || consumedDice < 1 || consumedDice > 6) {

            log.error(
                    "[INVALID_DICE] Player:{} Dice:{}",
                    userId,
                    consumedDice
            );

            throw new InvalidMoveException("Invalid consumed dice");
        }

        PlayerDTO currentPlayer = null;

        for (PlayerDTO player : gameState.getPlayers()) {

            if (player.getPlayerId().equals(userId)) {
                currentPlayer = player;
                break;
            }
        }

        if (currentPlayer == null) {

            log.error("[PLAYER_NOT_FOUND] Player:{}", userId);

            throw new InvalidMoveException("Player not found");
        }

        if (currentPlayer.getPendingDice() == null
                || !currentPlayer.getPendingDice().contains(consumedDice)) {

            log.error(
                    "[DICE_NOT_AVAILABLE] Player:{} Dice:{} Buffer:{}",
                    userId,
                    consumedDice,
                    currentPlayer.getPendingDice()
            );

            throw new InvalidMoveException(
                    "Selected dice not available in pending buffer"
            );
        }
    }

    private void validateBaseExitRule(
            GameStateDTO gameState,
            Long userId,
            Long tokenId,
            Integer consumedDice
    ) {

        for (PlayerDTO player : gameState.getPlayers()) {

            if (!player.getPlayerId().equals(userId)) {
                continue;
            }

            for (TokenDTO token : player.getTokens()) {

                if (!token.getTokenId().equals(tokenId)) {
                    continue;
                }

                if (token.getState() == TokenStateEnum.BASE
                        && consumedDice != 6) {

                    log.error(
                            "[BASE_EXIT_FAILED] Player:{} Token:{} Dice:{}",
                            userId,
                            tokenId,
                            consumedDice
                    );

                    throw new InvalidMoveException(
                            "Dice 6 required to leave base"
                    );
                }
            }
        }

        log.info(
                "[BASE_EXIT_VALIDATED] Player:{} Token:{}",
                userId,
                tokenId
        );
    }

    private void validateHomePathOvershoot(
            GameStateDTO gameState,
            Long userId,
            Long tokenId,
            Integer consumedDice
    ) {

        for (PlayerDTO player : gameState.getPlayers()) {

            if (!player.getPlayerId().equals(userId)) {
                continue;
            }

            for (TokenDTO token : player.getTokens()) {

                if (!token.getTokenId().equals(tokenId)) {
                    continue;
                }

                if (token.getState() == TokenStateEnum.HOME_PATH) {

                    int newPos = token.getPosition() + consumedDice;

                    if (newPos > BoardConstants.HOME_PATH_SIZE - 1) {

                        log.error(
                                "[HOME_PATH_OVERSHOOT] Player:{} Token:{} CurrentPos:{} Dice:{} NewPos:{}",
                                userId,
                                tokenId,
                                token.getPosition(),
                                consumedDice,
                                newPos
                        );

                        throw new InvalidMoveException(
                                "HOME_PATH overshoot"
                        );
                    }
                }
            }
        }

        log.info(
                "[HOME_PATH_VALIDATED] Player:{} Token:{}",
                userId,
                tokenId
        );
    }
}