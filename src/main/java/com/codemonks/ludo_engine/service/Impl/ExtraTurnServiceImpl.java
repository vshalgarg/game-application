package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.service.ExtraTurnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExtraTurnServiceImpl implements ExtraTurnService {

    @Override
    public boolean hasExtraTurn(
            Integer consumedDice,
            boolean tokenKilled,
            boolean tokenFinished
    ) {

        log.info(
                "[EXTRA_TURN_CHECK] Dice:{} TokenKilled:{} TokenFinished:{}",
                consumedDice,
                tokenKilled,
                tokenFinished
        );

        if (consumedDice == 6) {

            log.info("[EXTRA_TURN_GRANTED] Reason:DICE_SIX");

            return true;
        }

        if (tokenKilled) {

            log.info("[EXTRA_TURN_GRANTED] Reason:TOKEN_KILLED");

            return true;
        }

        if (tokenFinished) {

            log.info("[EXTRA_TURN_GRANTED] Reason:TOKEN_FINISHED");

            return true;
        }

        log.info("[EXTRA_TURN_DENIED]");

        return false;
    }
}
