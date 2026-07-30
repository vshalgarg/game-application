package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.PathOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.codemonks.ludo_engine.constant.LudoErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
public class PathOrderServiceImpl implements PathOrderService {

    @Override
    public Integer getPathOrder(GameStateDTO gameState, Long playerId
    ) {

        List<PlayerDTO> players = gameState.getPlayers();

        for (PlayerDTO player : players) {

            if (player.getPlayerId().equals(playerId)) {

                log.debug("[PATH_ORDER] Player:{} PathOrder:{}",
                        playerId,
                        player.getColorIndex());

                return player.getColorIndex();
            }
        }

        log.error(
                "[PLAYER_NOT_FOUND] Player:{}",
                playerId
        );

        throw new InvalidMoveException(INVALID_MOVE);
    }
}