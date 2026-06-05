package com.codemonks.api_gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

     // Creates a reusable WebClient bean.
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}