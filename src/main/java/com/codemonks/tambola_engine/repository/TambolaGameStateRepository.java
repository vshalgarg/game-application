package com.codemonks.tambola_engine.repository;


import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface TambolaGameStateRepository {
    TambolaGameState findById(Long roomId);
    void insert(TambolaGameState room);
    boolean updateIfVersionMatches(Long roomId, Map<String, Object> changes, Long expectedVersion);
    List<TambolaGameState> findRoomsDueForTick(Instant now);
}