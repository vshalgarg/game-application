package com.codemonks.gameservice.service.impl;

import com.codemonks.gameengine.dto.responseDto.GameStateResponse;
import com.codemonks.gameservice.service.SupabaseService;
import org.springframework.stereotype.Service;

@Service
public class SupabaseServiceImpl implements SupabaseService {
    @Override
    public GameStateResponse getGameState(Long roomId) {
        return null;
    }

    @Override
    public void saveGameState(Long roomId, GameStateResponse state) {

    }
}
