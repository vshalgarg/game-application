package com.codemonks.tambola_engine.service;


import com.codemonks.tambola_engine.domain.game.TambolaGameState;

// Tambola ke poore in-memory state ko Supabase ke realtime_game_state
// table me persist karne ki responsibility - clients (frontend) is
// table pe Supabase-realtime subscribe karke live updates dekhte hain.
public interface SupabaseRealtimeService {

    /**
     * Poora TambolaGameState ko ek hi row me upsert karta hai
     * (insert agar naya hai, update agar roomId already exist karta hai -
     * "resolution=merge-duplicates" isi ko handle karta hai).
     *
     * @param state jo room ka state persist karna hai
     */
    void upsertGameState(TambolaGameState state);
}