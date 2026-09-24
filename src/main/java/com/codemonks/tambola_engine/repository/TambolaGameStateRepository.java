package com.codemonks.tambola_engine.repository;

import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface TambolaGameStateRepository {
    TambolaGameState findById(Long roomId);
    void insert(TambolaGameState room);
    boolean updateIfVersionMatches(Long roomId, Map<String, Object> changes, Long expectedVersion);

    // NOTE: pg_cron (Supabase-side, process_due_tambola_rooms()) ab
    // ye responsibility handle karta hai. Ye method sirf isliye rakha
    // gaya hai taaki TimerServiceImpl (legacy Java-poller) rollback-fallback
    // ke roop me kaam kar sake agar kabhi pg_cron disable/fail ho jaye.
    List<TambolaGameState> findRoomsDueForTick(Instant now);
}