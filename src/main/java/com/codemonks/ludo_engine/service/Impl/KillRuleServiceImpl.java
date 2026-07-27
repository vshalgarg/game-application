package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.KillRuleResultDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.common.TokenDTO;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.service.BoardService;
import com.codemonks.ludo_engine.service.KillRuleService;
import com.codemonks.ludo_engine.service.PathOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KillRuleServiceImpl implements KillRuleService {

    private final BoardService boardService;
    private final PathOrderService pathOrderService;

    @Override
    public KillRuleResultDTO processKillRule(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId) {

        log.info("[KILL_RULE_STARTED] Player:{} Token:{}", playerId, tokenId);

        KillRuleResultDTO result = new KillRuleResultDTO();
        result.setTokenKilled(false);
        Integer currentCellId = null;

        for (PlayerDTO player : gameState.getPlayers()) {
            if (!player.getPlayerId().equals(playerId)) {
                continue;}

            Integer pathOrder =
                    pathOrderService.getPathOrder(
                            gameState,
                            player.getPlayerId()
                    );

            List<Integer> path =
                    boardService.getPath(pathOrder);


            if (path == null || path.isEmpty()) {

                log.error("[BOARD_PATH_NOT_FOUND] ColorIndex:{}", player.getColorIndex());
                result.setGameState(gameState);
                return result;
            }

            for (TokenDTO token : player.getTokens()) {

                if (!token.getTokenId().equals(tokenId)) {
                    continue;
                }

                if (token.getState() != TokenStateEnum.TRACK) {
                    result.setGameState(gameState);
                    return result;
                }

                if (token.getPathIndex() == null) {

                    log.error(
                            "[TOKEN_PATH_INDEX_NULL] Player:{} Token:{}",
                            playerId,
                            tokenId
                    );

                    result.setGameState(gameState);
                    return result;
                }
                if (token.getPathIndex() >= path.size()) {

                    log.error(
                            "[INVALID_PATH_INDEX] Player:{} Token:{} PathIndex:{}",
                            playerId,
                            tokenId,
                            token.getPathIndex()
                    );
                    result.setGameState(gameState);
                    return result;
                }
                currentCellId = path.get(token.getPathIndex());
                break;
            }
            break;
        }

        if (currentCellId == null) {
            log.error(
                    "[KILL_RULE_FAILED] Token position not found. Player:{} Token:{}",
                    playerId,
                    tokenId
            );
            result.setGameState(gameState);
            return result;
        }
        if (boardService.getSafeCells().contains(currentCellId)) {
            log.info("[SAFE_CELL] Cell:{} Kill not allowed", currentCellId);
            result.setGameState(gameState);
            return result;
        }

        for (PlayerDTO player : gameState.getPlayers()) {
            if (player.getPlayerId().equals(playerId)) {
                continue;
            }

            List<Integer> victimPath = boardService.getPath(player.getColorIndex());

            if (victimPath == null || victimPath.isEmpty()) {

                log.error("[BOARD_PATH_NOT_FOUND] ColorIndex:{}", player.getColorIndex());
                result.setGameState(gameState);
                return result;
            }
            for (TokenDTO token : player.getTokens()) {
                if (token.getState() != TokenStateEnum.TRACK
                        || token.getPathIndex() == null) {
                    continue;
                }
                if (token.getPathIndex() >= victimPath.size()) {
                    continue;
                }

                Integer victimCellId = victimPath.get(token.getPathIndex());
                if (!victimCellId.equals(currentCellId)) {
                    continue;
                }

                token.setState(TokenStateEnum.BASE);
                token.setPathIndex(null);

                result.setTokenKilled(true);
                result.setKilledPlayerId(player.getPlayerId());
                result.setKilledTokenId(token.getTokenId());

                log.info(
                        "[TOKEN_KILLED] VictimPlayer:{} VictimToken:{} Cell:{}",
                        player.getPlayerId(),
                        token.getTokenId(),
                        currentCellId
                );

                result.setGameState(gameState);
                log.info("[KILL_RULE_COMPLETED] TokenKilled:true");
                return result;
            }
        }
        result.setGameState(gameState);
        log.info("[KILL_RULE_COMPLETED] TokenKilled:{}", result.isTokenKilled());
        return result;
    }
}