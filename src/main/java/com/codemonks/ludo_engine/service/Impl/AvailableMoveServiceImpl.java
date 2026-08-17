package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.*;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailableMoveServiceImpl implements AvailableMoveService {

    private final BoardService boardService;
    private final PathOrderService pathOrderService;
    private final TokenMovementService tokenMovementService;
    private final KillRuleService killRuleService;
    private final ObjectMapper objectMapper;

    @Override
    public List<LegalMoveDTO> getAvailableMoves(
            GameStateDTO gameState,
            PlayerDTO player,
            List<Integer> pendingDice
    ) {

        List<LegalMoveDTO> legal = new ArrayList<>();

        if (pendingDice == null) {
            return legal;
        }

        Integer pathOrder = pathOrderService.getPathOrder(gameState, player.getPlayerId());
        List<Integer> path = boardService.getPath(pathOrder);

        if (path == null || path.isEmpty()) {
            log.error(
                    "[COMPUTE_LEGAL_MOVES] Board path not found. Player:{} ColorIndex:{}",
                    player.getPlayerId(),
                    player.getColorIndex()
            );
            return legal;
        }

        Set<Integer> safeCells = boardService.getSafeCells();

        for (Integer dice : pendingDice) {
            for (TokenDTO token : player.getTokens()) {

                if (!isTokenMovable(token, dice, path)) {
                    continue;
                }
                legal.add(evaluateCandidate(gameState, player, token, dice, safeCells));
            }
        }
        return legal;
    }

    private LegalMoveDTO evaluateCandidate(
            GameStateDTO gameState,
            PlayerDTO player,
            TokenDTO token,
            Integer dice,
            Set<Integer> safeCells
    ) {

        boolean exitsBase = token.getState() == TokenStateEnum.BASE;

        LegalMoveDTO.LegalMoveDTOBuilder builder = LegalMoveDTO.builder()
                .tokenId(token.getTokenId())
                .dice(dice)
                .exitsBase(exitsBase);

        try {
            GameStateDTO clonedState = deepClone(gameState);

            GameStateDTO afterMove = tokenMovementService.moveToken(
                            clonedState,
                            player.getPlayerId(),
                            token.getTokenId(),
                            dice
                    );

            TokenDTO movedToken = findToken(afterMove, player.getPlayerId(), token.getTokenId());

            if (movedToken == null) {
                log.warn(
                        "[EVALUATE_CANDIDATE_TOKEN_MISSING] Player:{} Token:{} Dice:{}",
                        player.getPlayerId(), token.getTokenId(), dice
                );
                return builder.build();
            }

            boolean reachesHome = movedToken.getState() == TokenStateEnum.FINISHED;
            Integer resultingPathIndex = movedToken.getPathIndex();
            Integer destinationCellId = movedToken.getPathId();
            boolean landsOnSafeCell = destinationCellId != null && safeCells.contains(destinationCellId);

            KillRuleResultDTO killResult =
                    killRuleService.processKillRule(afterMove, player.getPlayerId(), token.getTokenId());

            return builder
                    .reachesHome(reachesHome)
                    .resultingPathIndex(resultingPathIndex)
                    .landsOnSafeCell(landsOnSafeCell)
                    .killsOpponent(killResult.isTokenKilled())
                    .killedTokenId(killResult.getKilledTokenId())
                    .killedPlayerId(killResult.getKilledPlayerId())
                    .build();

        } catch (Exception exception) {
            log.warn(
                    "[EVALUATE_CANDIDATE_FAILED] Player:{} Token:{} Dice:{} Reason:{}",
                    player.getPlayerId(), token.getTokenId(), dice, exception.getMessage()
            );
            return builder.build();
        }
    }

    private GameStateDTO deepClone(GameStateDTO gameState) {
        Map<String, Object> asMap = objectMapper.convertValue(gameState, Map.class);
        return objectMapper.convertValue(asMap, GameStateDTO.class);
    }

    private TokenDTO findToken(GameStateDTO state, Long playerId, Long tokenId) {
        for (PlayerDTO p : state.getPlayers()) {
            if (!p.getPlayerId().equals(playerId)) continue;
            for (TokenDTO t : p.getTokens()) {
                if (t.getTokenId().equals(tokenId)) return t;
            }
        }
        return null;
    }

    @Override
    public boolean hasAnyLegalMove(GameStateDTO gameState, PlayerDTO player, int diceNumber) {

        Integer pathOrder = pathOrderService.getPathOrder(gameState, player.getPlayerId());
        List<Integer> path = boardService.getPath(pathOrder);

        if (path == null || path.isEmpty()) {
            log.error(
                    "[HAS_ANY_LEGAL_MOVE] Board path not found. Player:{} ColorIndex:{}",
                    player.getPlayerId(), player.getColorIndex()
            );
            return false;
        }

        for (TokenDTO token : player.getTokens()) {
            if (isTokenMovable(token, diceNumber, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTokenMovable(TokenDTO token, int diceNumber, List<Integer> path) {

        if (token.getState() == TokenStateEnum.FINISHED) {
            return false;
        }

        if (token.getState() == TokenStateEnum.BASE) {
            return diceNumber == 6;
        }

        if (token.getState() == TokenStateEnum.TRACK) {
            Integer currentIndex = token.getPathIndex();
            if (currentIndex == null || currentIndex < 0 || currentIndex >= path.size()) {
                log.warn(
                        "[INVALID_TOKEN_PATH_INDEX] Token:{} PathIndex:{} PathSize:{}",
                        token.getTokenId(), currentIndex, path.size()
                );
                return false;
            }

            int newIndex = currentIndex + diceNumber;
            return newIndex < path.size();
        }

        return false;
    }
}