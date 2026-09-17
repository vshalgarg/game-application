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

    // Tambola apna dedicated game-state table use karta hai
    // (realtime_tambola_game_state) - shared realtime_game_state me
    // ab kuch NAHI likhta. Room lobby sirf realtime_room_lobby se
    // broadcast hota hai. Tickets/claims/rules apne apne tables me.
    @Getter
    @Setter
    public static class Tables {
        private String realtimeGameState;
        private String realtimeRoomLobby;
        private String realtimeTambolaRules;        // realtime_tambola_rules
        private String tambolaTickets;              // tambola_tickets (persisted only)
        private String tambolaClaims;
        private String realtimeTambolaGameState;    // realtime_tambola_game_state
    }
}