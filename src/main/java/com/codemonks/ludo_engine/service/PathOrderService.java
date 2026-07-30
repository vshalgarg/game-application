package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;

public interface PathOrderService {

    Integer getPathOrder(
            GameStateDTO gameState,
            Long playerId
    );
}