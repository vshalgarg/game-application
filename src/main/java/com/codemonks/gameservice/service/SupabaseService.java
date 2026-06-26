package com.codemonks.gameservice.service;


import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;

public interface SupabaseService {
    void upsertLobbyState(RealtimeLobbyDTO lobbyDTO);

}
