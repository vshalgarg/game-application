package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.TurnValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.codemonks.ludo_engine.constant.LudoErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
public class TurnValidationServiceImpl  implements TurnValidationService {

    @Override
    public void validateTurn(GameStateDTO gameState, Long playerId){

        if (!gameState.getCurrentTurnPlayerId().equals(playerId)) {

            throw new InvalidMoveException(INVALID_MOVE);
        }
        log.info("Turn validation passed for player:{}", playerId);
    }
}
