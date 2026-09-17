package com.codemonks.tambola_engine.service;



import com.codemonks.tambola_engine.dto.realtime.RealtimeLobbyDTO;

public interface SupabaseRealtimeService {

    void publishLobbyState(RealtimeLobbyDTO lobbyDTO);
}