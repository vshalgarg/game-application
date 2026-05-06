package com.codemonks.gameservice.service;


import com.codemonks.gameservice.dto.response.GameStateResponse;

public interface SupabaseService {
    GameStateResponse getGameState(Long roomId);
    void saveGameState(Long roomId, GameStateResponse state);
}
