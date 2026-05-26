package com.codemonks.gameservice.service;


import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeMoveDTO;

public interface SupabaseService {
    void upsertLobbyState(RealtimeLobbyDTO lobbyDTO);
    void upsertGameState(RealtimeGameStateDTO gameStateDTO);
    RealtimeGameStateDTO getGameState(Long roomId);
    void saveMove(RealtimeMoveDTO moveDTO);
}
