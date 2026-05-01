package com.codemonks.gameservice.service;

import com.codemonks.gameengine.dto.responseDto.GameStateResponse;

public interface SupabaseService {
    GameStateResponse getGameState(Long roomId);
    void saveGameState(Long roomId, GameStateResponse state);
}
