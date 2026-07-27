package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.PathOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.codemonks.ludo_engine.constant.ErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
public class PathOrderServiceImpl implements PathOrderService {

    @Override
    public Integer getPathOrder(
            GameStateDTO gameState,
            Long playerId
    ) {

        List<PlayerDTO> players = gameState.getPlayers();

        for (int i = 0; i < players.size(); i++) {

            PlayerDTO player = players.get(i);

            if (player.getPlayerId().equals(playerId)) {

                log.debug(
                        "[PATH_ORDER] Player:{} PathOrder:{}",
                        playerId,
                        i
                );

                return i;
            }
        }

        log.error(
                "[PLAYER_NOT_FOUND] Player:{}",
                playerId
        );

        throw new InvalidMoveException(INVALID_MOVE);
    }
}