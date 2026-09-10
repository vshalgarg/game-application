package com.codemonks.tambola_engine.domain.game;


/**
 * Central in-memory registry of all currently live Tambola rooms.
 * <p>
 * This is the single source of truth for "which TambolaGameState
 * belongs to which roomId" - TimerService, ClaimService, and any
 * controller/service that needs to read or mutate a room's state
 * all go through this registry instead of maintaining their own
 * separate maps, which would risk drifting out of sync with each
 * other.
 */
public interface GameStateRegistry {

    /**
     * Registers a newly created room's state, making it live.
     * Typically called right after a room is set up (game start).
     *
     * @param state the freshly initialized state for the room
     */
    void register(TambolaGameState state);

    /**
     * Fetches the live state for a room.
     *
     * @param roomId the room to fetch
     * @return the room's current TambolaGameState
     * @throws IllegalStateException if no state is registered for this roomId
     */
    TambolaGameState get(Long roomId);

    /**
     * Removes a room's state once its game is FINISHED and no
     * longer needs to be tracked in memory.
     * @param roomId the room to remove
     */
    void remove(Long roomId);
}