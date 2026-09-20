package com.codemonks.tambola_engine.service;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;

public interface GameSetupService {
    GameSetupResult initializeGame(EngineStartGameRequestDTO request);
}