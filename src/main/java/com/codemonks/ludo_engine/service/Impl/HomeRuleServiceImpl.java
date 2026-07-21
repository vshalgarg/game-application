package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.constant.BoardConstants;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.HomeRuleResultDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.common.TokenDTO;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.service.HomeRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HomeRuleServiceImpl implements HomeRuleService {

    @Override
    public HomeRuleResultDTO processHomeRule(
            GameStateDTO gameState,
            Long playerId,
            Long tokenId
    ) {

        log.info(
                "[HOME_RULE_STARTED] Player:{} Token:{}",
                playerId,
                tokenId
        );

        HomeRuleResultDTO result = new HomeRuleResultDTO();
        result.setReachedHome(false);

        for (PlayerDTO player : gameState.getPlayers()) {

            if (!player.getPlayerId().equals(playerId)) {
                continue;
            }

            for (TokenDTO token : player.getTokens()) {

                if (!token.getTokenId().equals(tokenId)) {
                    continue;
                }

                if (token.getState() == TokenStateEnum.HOME_PATH
                        && token.getPosition() == BoardConstants.HOME_PATH_SIZE - 1) {

                    token.setState(TokenStateEnum.FINISHED);
                    token.setPosition(null);

                    result.setReachedHome(true);
                    result.setPlayerId(playerId);
                    result.setTokenId(tokenId);

                    log.info(
                            "[TOKEN_FINISHED] Player:{} Token:{}",
                            playerId,
                            tokenId
                    );
                }

                break;
            }

            break;
        }

        result.setGameState(gameState);

        log.info(
                "[HOME_RULE_COMPLETED] ReachedHome:{}",
                result.isReachedHome()
        );

        return result;
    }
}