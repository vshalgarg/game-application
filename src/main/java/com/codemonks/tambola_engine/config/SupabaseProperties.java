package com.codemonks.tambola_engine.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// Named bean - Ludo ke SupabaseProperties se clash na ho isliye.
@Configuration("tambolaSupabaseProperties")
@Getter
@Setter
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {

    private String url;
    private String apiKey;
    private Tables tables;

    // IMPORTANT: Ye Ludo ke SAME generic tables hain (realtime_game_state,
    // realtime_room_lobby) - Tambola ka apna alag ticket/claim table NAHI
    // hai. Poora TambolaGameState (calledNumbers, tickets, claims, rules)
    // realtime_game_state ke "game_state_data" jsonb column ke andar
    // ek hi nested-object ke roop me jaata hai - bilkul jaisa Ludo apna
    // poora board/token-state isi column me daalta hai.
    @Getter
    @Setter
    public static class Tables {
        private String realtimeGameState;
        private String realtimeRoomLobby;
    }
}