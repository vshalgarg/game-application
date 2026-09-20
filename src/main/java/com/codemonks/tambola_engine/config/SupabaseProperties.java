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

    @Getter
    @Setter
    public static class Tables {
        private String realtimeGameState;
        private String realtimeRoomLobby;
        private String realtimeTambolaRules;
        private String tambolaTickets;
        private String tambolaClaims;
        private String realtimeTambolaGameState;
    }
}