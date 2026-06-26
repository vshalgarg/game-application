package com.codemonks.tic_tac_toe_game_engine.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
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
        //  private String realtimeRoomLobby;
        private String realtimeGameState;
        private String realtimeGameMoves;
    }
}
