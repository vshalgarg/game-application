package com.codemonks.ludo_engine.bot.strategy;

import com.codemonks.ludo_engine.dto.common.BotDecisionDTO;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.request.EngineMoveRequestDTO;

import java.util.List;


public interface BotStrategy {

    BotDecisionDTO chooseMove(
            GameStateDTO gameState,
            Long botPlayerId,
            List<Integer> pendingDice
    );
}
