package com.codemonks.gameservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SupabaseConfig {

    private final SupabaseProperties properties;

    @Bean
    public WebClient supabaseWebClient() {
      log.info("supabase url: {} api key: {}", properties.getUrl(), properties.getApiKey());
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
