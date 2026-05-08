package com.codemonks.gameservice.service;


import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeMoveDTO;

public interface SupabaseService {
    void createInitialState(
            RealtimeGameStateDTO realtimeGameStateDTO
    );

    RealtimeGameStateDTO getGameState(
            String gameId
    );

    void updateGameState(
            RealtimeGameStateDTO realtimeGameStateDTO
    );

    void saveMove(RealtimeMoveDTO moveDTO);
}
