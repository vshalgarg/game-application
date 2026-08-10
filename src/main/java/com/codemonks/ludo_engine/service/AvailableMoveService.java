package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.LegalMoveDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;

import java.util.List;

public interface AvailableMoveService {

    List<LegalMoveDTO> getAvailableMoves(
            GameStateDTO gameState,
            PlayerDTO player,
            List<Integer> pendingDice
    );

    boolean hasAnyLegalMove(
            GameStateDTO gameState,
            PlayerDTO player,
            int diceNumber
    );
}