package com.codemonks.tambola_engine.service;


import com.codemonks.tambola_engine.domain.game.TambolaGameState;

/**
 * Responsible for deciding "which number comes next" for a given
 * room's Tambola game, and recording it into that room's
 * TambolaGameState.
 * <p>
 * Deliberately separate from TimerService: this service answers
 * "what is the next number", TimerService answers "when should the
 * next number be generated" (see the per-room dynamic-interval
 * scheduling design). This keeps a single call here equal to a
 * single generated number - it is not itself a scheduling loop.
 */
public interface NumberGeneratorService {

    /**
     * Picks a number that has not yet been called in this room,
     * records it into the room's TambolaGameState via callNumber(),
     * and returns it.
     *
     * @param state the live game state for the room to generate a number for
     * @return the newly called number (1-90)
     * @throws com.codemonks.tambola_engine.exception.BoardExhaustedException
     *         if all 90 numbers have already been called for this room
     */
    Integer generateNextNumber(TambolaGameState state);
}