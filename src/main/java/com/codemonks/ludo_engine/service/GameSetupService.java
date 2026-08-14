package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;

public interface GameSetupService {

    GameStateDTO initializeGame(EngineStartGameRequestDTO request);

}
