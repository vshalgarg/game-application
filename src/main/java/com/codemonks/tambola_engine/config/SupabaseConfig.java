package com.codemonks.tambola_engine.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// Named bean/config - "tambola" prefix isliye zaroori hai kyunki ye
// poora project ek hi Spring context me chalta hai (modular monolith:
// ludo_engine, tambola_engine, tic_tac_toe_game_engine sab saath).
// Agar generic naam rakhte (jaisa "supabaseConfig"), to Ludo ke
// SupabaseConfig ke saath naam-clash ho jaata aur app start hi nahi hoti
// (BeanDefinitionOverrideException - jaisa Ludo me @EnableJpaAuditing
// duplicate hone par pehle hua tha).
@Configuration("tambolaSupabaseConfig")
@RequiredArgsConstructor
public class SupabaseConfig {

    private final SupabaseProperties properties;

    @Bean("tambolaSupabaseRestClient")
    public RestClient supabaseRestClient() {

        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader("apikey", properties.getApiKey())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .build();
    }
}