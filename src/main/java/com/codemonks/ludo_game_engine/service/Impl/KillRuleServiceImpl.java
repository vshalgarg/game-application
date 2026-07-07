package com.codemonks.ludo_game_engine.service.Impl;

import com.codemonks.ludo_game_engine.constant.BoardConstants;
import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.common.KillRuleResultDTO;
import com.codemonks.ludo_game_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_game_engine.dto.common.TokenDTO;
import com.codemonks.ludo_game_engine.enums.TokenStateEnum;
import com.codemonks.ludo_game_engine.service.KillRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KillRuleServiceImpl implements KillRuleService {

    @Override
    public KillRuleResultDTO processKillRule(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId
    ) {

        log.info(
                "[KILL_RULE_STARTED] Player:{} Token:{}",
                playerId,
                tokenId
        );

        KillRuleResultDTO result = new KillRuleResultDTO();
        result.setTokenKilled(false);

        Integer currentPosition = null;

        for (PlayerDTO player : gameState.getPlayers()) {

            if (!player.getPlayerId().equals(playerId)) {
                continue;
            }

            for (TokenDTO token : player.getTokens()) {

                if (token.getTokenId().equals(tokenId)) {
                    currentPosition = token.getPosition();
                    break;
                }
            }

            break;
        }

        if (currentPosition == null) {

            log.error(
                    "[KILL_RULE_FAILED] Token position not found. Player:{} Token:{}",
                    playerId,
                    tokenId
            );

            result.setGameState(gameState);
            return result;
        }

        if (BoardConstants.SAFE_CELLS.contains(currentPosition)) {

            log.info(
                    "[SAFE_CELL] Position:{} Kill not allowed",
                    currentPosition
            );

            result.setGameState(gameState);
            return result;
        }

        for (PlayerDTO player : gameState.getPlayers()) {

            if (player.getPlayerId().equals(playerId)) {
                continue;
            }

            for (TokenDTO token : player.getTokens()) {

                if (token.getState() == TokenStateEnum.TRACK
                        && token.getPosition() != null
                        && token.getPosition().equals(currentPosition)) {

                    token.setState(TokenStateEnum.BASE);
                    token.setPosition(-1);
                    result.setTokenKilled(true);
                    result.setKilledPlayerId(player.getPlayerId());
                    result.setKilledTokenId(token.getTokenId());

                    log.info(
                            "[TOKEN_KILLED] VictimPlayer:{} VictimToken:{} Position:{}",
                            player.getPlayerId(),
                            token.getTokenId(),
                            currentPosition
                    );
                }
            }
        }

        result.setGameState(gameState);
        log.info(
                "[KILL_RULE_COMPLETED] TokenKilled:{}",
                result.isTokenKilled()
        );
        return result;
    }
}