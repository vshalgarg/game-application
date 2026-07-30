package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.ludo_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.ludo_engine.dto.realtime.RealtimeMoveDTO;

public interface SupabaseRealtimeService {

    RealtimeGameStateDTO getGameState(Long roomId);

    void upsertGameState(RealtimeGameStateDTO state);

    void saveMove(RealtimeMoveDTO moveDTO);

    void publishLobbyState(RealtimeLobbyDTO lobbyDTO);
}