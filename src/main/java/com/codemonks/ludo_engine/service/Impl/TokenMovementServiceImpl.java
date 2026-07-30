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

import java.util.ArrayList;
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
                if (token.getState() == TokenStateEnum.BASE) {

                    if (consumedDice != 6) {
                        throw new InvalidMoveException(INVALID_MOVE);
                    }

                    token.setState(TokenStateEnum.TRACK);
                    token.setPathIndex(0);
                   // consumePendingDiceByIndex(player, consumedDice);
                    Integer colorIndex = pathOrderService.getPathOrder(
                            gameState,
                            player.getPlayerId()
                    );

                    List<Integer> path = boardService.getPath(colorIndex);
                    token.setPathId(path.get(0)); // NEW

                    List<Integer> journey = new ArrayList<>();
                    if (token.getBaseSlotId() != null) {
                        journey.add(token.getBaseSlotId());
                    }
                    journey.add(path.get(0));
                    token.setTokenJourney(journey);

                    consumePendingDiceByIndex(player, consumedDice);

                    log.info(
                            "[BASE_TO_TRACK] Player:{} Token:{} PathIndex:{}  Journey:{}",
                            playerId,
                            tokenId,
                            token.getPathIndex(),
                            token.getTokenJourney()
                    );

                    return gameState;
                }
                // TRACK movement
                if (token.getState() == TokenStateEnum.TRACK) {

                    if (token.getPathIndex() == null) {
                        throw new InvalidMoveException(INVALID_MOVE);
                    }

                    Integer colorIndex = pathOrderService.getPathOrder(
                                    gameState,
                                    player.getPlayerId()
                            );

                    List<Integer> path = boardService.getPath(colorIndex);

                    log.info(
                            "[TRACK_MOVE_STARTED] Player:{} Token:{} CurrentPathIndex:{} Dice:{} Journey:{}",
                            playerId,
                            tokenId,
                            token.getPathIndex(),
                            consumedDice,
                            token.getTokenJourney()
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
                    // Append every intermediate cell crossed this move — THE FIX
                    List<Integer> journey = token.getTokenJourney();
                    if (journey == null) {
                        journey = new ArrayList<>();
                    }
                    for (int i = currentPathIndex + 1; i <= newPathIndex; i++) {
                        journey.add(path.get(i));
                    }
                    token.setTokenJourney(journey);

                    // Token reached goal
                    if (newPathIndex == path.size() - 1) {
                        token.setPathIndex(newPathIndex);
                        token.setState(TokenStateEnum.FINISHED);
                        consumePendingDiceByIndex(player, consumedDice);

                        log.info(
                                "[TOKEN_FINISHED] Player:{} Token:{} PathIndex:{}  PathId:{} Journey:{}",
                                playerId,
                                tokenId,
                                newPathIndex,
                                token.getPathId(),
                                token.getTokenJourney()
                        );

                        return gameState;
                    }

                    // Normal movement
                    token.setPathIndex(newPathIndex);
                    token.setPathId(path.get(newPathIndex));
                    consumePendingDiceByIndex(player, consumedDice);

                    log.info(
                            "[TRACK_MOVED] Player:{} Token:{} FromPathIndex:{} ToPathIndex:{} PathId:{}",
                            playerId,
                            tokenId,
                            currentPathIndex,
                            newPathIndex,
                            token.getPathId()
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

        boolean removed = player.getPendingDice()
                        .remove(consumedDice);

        log.info("[BUFFER_UPDATED] RemovedDice:{} Success:{} Remaining:{}",
                consumedDice,
                removed,
                player.getPendingDice()
        );
    }
}