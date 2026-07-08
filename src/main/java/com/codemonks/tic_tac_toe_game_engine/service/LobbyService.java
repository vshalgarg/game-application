package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeLobbyDTO;

public interface LobbyService {
    void publishLobbyState(RealtimeLobbyDTO request);
}
