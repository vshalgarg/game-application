package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeMoveDTO;

public interface SupabaseRealtimeService {

    RealtimeGameStateDTO getGameState(Long roomId);

    void upsertGameState(RealtimeGameStateDTO state);

    void saveMove(RealtimeMoveDTO moveDTO);
}