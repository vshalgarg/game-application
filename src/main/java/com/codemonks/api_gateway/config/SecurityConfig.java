package com.codemonks.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())

                .authorizeExchange(exchange -> exchange

                        // Allow ALL preflight requests
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public auth APIs
                        .pathMatchers("/game-gateway/auth/**").permitAll()

                        // Protected APIs
                        .pathMatchers("/game-gateway/game-service/**").authenticated()

                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults())
                )

                .build();
    }
}
