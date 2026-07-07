package com.codemonks.ludo_game_engine.service.Impl;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.exception.InvalidMoveException;
import com.codemonks.ludo_game_engine.service.TurnValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TurnValidationServiceImpl  implements TurnValidationService {

    @Override
    public void validateTurn(GameStateDTO gameState,Long playerId){

        if (!gameState.getCurrentTurnPlayerId().equals(playerId)) {

            throw new InvalidMoveException("Not your turn");
        }
        log.info("Turn validation passed for player:{}", playerId);
    }
}
