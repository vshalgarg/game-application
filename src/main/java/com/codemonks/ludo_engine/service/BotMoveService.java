package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.request.EngineMoveRequestDTO;

public interface BotMoveService {

    void processBotTurn(
            GameStateDTO gameState,
            Long roomId,
            String roomCode,
            Long botPlayerId
    );

}