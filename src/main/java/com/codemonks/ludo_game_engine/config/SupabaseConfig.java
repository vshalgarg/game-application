package com.codemonks.ludo_game_engine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class SupabaseConfig {

    private final SupabaseProperties properties;

    @Bean
    public RestClient supabaseRestClient() {

        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader(
                        "apikey",
                        properties.getApiKey()
                )
                .defaultHeader(
                        "Authorization",
                        "Bearer " + properties.getApiKey()
                )
                .build();
    }
}