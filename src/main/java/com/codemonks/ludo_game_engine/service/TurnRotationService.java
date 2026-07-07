package com.codemonks.ludo_game_engine.service;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;

public interface TurnRotationService {

    GameStateDTO updateTurn(GameStateDTO gameState, Long currentPlayerId, boolean extraTurn);

}

