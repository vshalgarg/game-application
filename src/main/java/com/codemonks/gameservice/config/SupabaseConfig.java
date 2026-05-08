package com.codemonks.gameservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class SupabaseConfig {

    private final SupabaseProperties properties;

    @Bean
    public WebClient supabaseWebClient() {

        return WebClient.builder()
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
