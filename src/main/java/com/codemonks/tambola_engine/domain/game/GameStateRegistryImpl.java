package com.codemonks.tambola_engine.domain.game;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default in-memory implementation of GameStateRegistry.
 * <p>
 * ConcurrentHashMap is used since rooms are registered, read, and
 * removed from multiple threads concurrently - HTTP request threads
 * (room setup, claims) and the TaskScheduler's own threads
 * (timer ticks) all access this map.
 */
@Component
public class GameStateRegistryImpl implements GameStateRegistry {

    private final Map<Long, TambolaGameState> rooms = new ConcurrentHashMap<>();

    @Override
    public void register(TambolaGameState state) {
        rooms.put(state.getRoomId(), state);
    }

    @Override
    public TambolaGameState get(Long roomId) {
        TambolaGameState state = rooms.get(roomId);
        if (state == null) {
            throw new IllegalStateException("No active game state found for room " + roomId);
        }
        return state;
    }

    @Override
    public void remove(Long roomId) {
        rooms.remove(roomId);
    }
}
