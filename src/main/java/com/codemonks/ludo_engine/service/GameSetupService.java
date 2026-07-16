package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;

public interface GameSetupService {

    //Responsibility-create initial  game setup,engine receive player and start prepare
    //color,token, first-turn,game state

    GameStateDTO initializeGame(EngineStartGameRequestDTO request);

}
