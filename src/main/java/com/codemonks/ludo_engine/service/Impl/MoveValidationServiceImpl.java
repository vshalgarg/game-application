    package com.codemonks.ludo_engine.service.Impl;

    import com.codemonks.ludo_engine.constant.LudoErrorCodesEnum;
    import com.codemonks.ludo_engine.dto.common.GameStateDTO;
    import com.codemonks.ludo_engine.dto.common.PlayerDTO;
    import com.codemonks.ludo_engine.dto.common.TokenDTO;
    import com.codemonks.ludo_engine.enums.TokenStateEnum;
    import com.codemonks.ludo_engine.exception.InvalidMoveException;
    import com.codemonks.ludo_engine.service.BoardService;
    import com.codemonks.ludo_engine.service.MoveValidationService;
    import com.codemonks.ludo_engine.service.PathOrderService;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import com.codemonks.ludo_engine.constant.LudoErrorCodesEnum;
    import java.util.List;


    @Service
    @Slf4j
    @RequiredArgsConstructor
    public class MoveValidationServiceImpl implements MoveValidationService {

        private final BoardService boardService;
        private final PathOrderService pathOrderService;

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
            validatePathLimit(gameState, userId, tokenId, consumedDice);

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
            throw new InvalidMoveException(LudoErrorCodesEnum.TOKEN_NOT_FOUND);
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

                throw new InvalidMoveException(LudoErrorCodesEnum.INVALID_DICE_VALUE_CONSUMPTION);
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

                throw new InvalidMoveException(LudoErrorCodesEnum.PLAYER_NOT_FOUND);
            }

            if (currentPlayer.getPendingDice() == null
                    || currentPlayer.getPendingDice().isEmpty()) {

                throw new InvalidMoveException(
                        LudoErrorCodesEnum.NO_PENDING_DICE);
            }
            if (!currentPlayer.getPendingDice().contains(consumedDice)) {

                log.error(
                        "[DICE_NOT_AVAILABLE] Player:{} Dice:{} Buffer:{}",
                        userId,
                        consumedDice,
                        currentPlayer.getPendingDice()
                );
                throw new InvalidMoveException(LudoErrorCodesEnum.INVALID_DICE_VALUE_CONSUMPTION);
            }
            log.info(
                    "[DICE_VALIDATED] Player:{} Dice:{} Buffer:{}",
                    userId,
                    consumedDice,
                    currentPlayer.getPendingDice()
            );
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
                    log.info(
                            "[BASE_EXIT_RULE] Player:{} Token:{} State:{} Dice:{}",
                            userId,
                            tokenId,
                            token.getState(),
                            consumedDice
                    );

                    if (token.getState() == TokenStateEnum.BASE
                            && consumedDice != 6) {

                        log.error(
                                "[BASE_EXIT_FAILED] Player:{} Token:{} Dice:{}",
                                userId,
                                tokenId,
                                consumedDice
                        );

                        throw new InvalidMoveException(LudoErrorCodesEnum.SIX_REQUIRED_TO_EXIT_HOME);
                    }
                }
            }

            log.info(
                    "[BASE_EXIT_VALIDATED] Player:{} Token:{}",
                    userId,
                    tokenId
            );
        }
        private void validatePathLimit(
                GameStateDTO gameState,
                Long userId,
                Long tokenId,
                Integer consumedDice
        ) {

            for (PlayerDTO player : gameState.getPlayers()) {

                if (!player.getPlayerId().equals(userId)) {
                    continue;
                }

                Integer pathOrder =
                        pathOrderService.getPathOrder(
                                gameState,
                                userId
                        );

                List<Integer> path =
                        boardService.getPath(pathOrder);
                if (path == null || path.isEmpty()) {

                    log.error(
                            "[BOARD_PATH_NOT_FOUND] PathOrder:{}",
                            pathOrder
                    );
                    throw new InvalidMoveException(LudoErrorCodesEnum.INVALID_PATH_INDEX);
                }

                for (TokenDTO token : player.getTokens()) {

                    if (!token.getTokenId().equals(tokenId)) {
                        continue;
                    }

                    if (token.getState() == TokenStateEnum.BASE
                            || token.getState() == TokenStateEnum.FINISHED) {

                        log.info(
                                "[PATH_LIMIT_SKIPPED] Player:{} Token:{} State:{}",
                                userId,
                                tokenId,
                                token.getState()
                        );

                        return;
                    }
                    if (token.getPathIndex() == null
                            || token.getPathIndex() < 0
                            || token.getPathIndex() >= path.size()) {

                        log.error(
                                "[INVALID_PATH_INDEX] Player:{} Token:{} PathIndex:{}",
                                userId,
                                tokenId,
                                token.getPathIndex()
                        );

                        throw new InvalidMoveException(LudoErrorCodesEnum.INVALID_PATH_INDEX);
                    }

                    int newPathIndex = token.getPathIndex() + consumedDice;

                    log.info(
                            "[PATH_LIMIT_CHECK] Player:{} Token:{} Current:{} Dice:{} Target:{} Max:{}",
                            userId,
                            tokenId,
                            token.getPathIndex(),
                            consumedDice,
                            newPathIndex,
                            path.size() - 1
                    );

                    if (newPathIndex > path.size() - 1) {

                        log.error(
                                "[PATH_LIMIT_FAILED] Player:{} Token:{}",
                                userId,
                                tokenId
                        );

                        throw new InvalidMoveException(LudoErrorCodesEnum.INVALID_PATH_INDEX);
                    }

                    log.info(
                            "[PATH_LIMIT_VALIDATED] Player:{} Token:{}",
                            userId,
                            tokenId
                    );

                    return;
                }
            }
        }
    }