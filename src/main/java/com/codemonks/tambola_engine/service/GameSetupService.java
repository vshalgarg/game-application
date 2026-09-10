package com.codemonks.tambola_engine.service;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;

// Room-setup ka entry point - jab game-service ek naya Tambola game start
// karna chahta hai, wo isi service ko call karta hai (via controller).
public interface GameSetupService {

    /**
     * Naye room ke liye poora setup karta hai: TambolaGameState banata hai,
     * har player ke liye tickets generate karta hai, GameStateRegistry me
     * register karta hai. Timer yahan START NAHI hota - wo alag explicit
     * "start game" action hai (status INITIALIZED se RUNNING hone par),
     * taaki setup aur actual-game-start do alag concerns rahein.
     *
     * @param request room, players, rules, timer-interval ki details
     * @return setup ka result - roomId, status, aur generated tickets
     */
    GameSetupResult initializeGame(EngineStartGameRequestDTO request);
}