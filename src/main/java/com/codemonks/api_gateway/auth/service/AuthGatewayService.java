package com.codemonks.api_gateway.auth.service;

import com.codemonks.api_gateway.auth.dto.request.AuthLoginRequest;
import com.codemonks.api_gateway.auth.dto.request.AuthRegisterRequest;
import com.codemonks.api_gateway.auth.dto.request.LoginRequest;
import com.codemonks.api_gateway.auth.dto.request.RegisterRequest;
import com.codemonks.api_gateway.auth.dto.response.LoginResponse;
import com.codemonks.api_gateway.auth.dto.response.RegisterResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthGatewayService {

    private final WebClient webClient;

    @Value("${auth.service.url}")
    private String authServiceUrl;
    @Value("${auth.service.client.name}")
    private String authServiceClientName;
    @Value("${auth.service.client.secret}")
    private String authServiceClientSecret;


    public Mono<RegisterResponse> register(RegisterRequest request) {

        log.info("[REGISTER] Request received for email={}", request.email());

        AuthRegisterRequest authRequest = new AuthRegisterRequest(
                        request.email(),
                        request.password(),
                        Collections.emptyList()
                );

        log.info("[REGISTER] Calling Auth Service endpoint={}", authServiceUrl + "/auth/api/v1/register");

        return webClient
                .post()
                .uri(authServiceUrl + "/auth/api/v1/register")
                .header("clientName", authServiceClientName)
                .header(
                        "clientSecret",
                        authServiceClientSecret
                )
                .bodyValue(authRequest)
                .retrieve()

                // Read raw response first
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("[REGISTER] RAW RESPONSE={}", response))

                // Temporary mapping just to keep compilation working
                .map(response -> new RegisterResponse(response))

                .doOnError(error ->
                        log.error("[REGISTER] Failed reason={}", error.getMessage(), error));
    }

    public Mono<LoginResponse> login(LoginRequest request) {

        log.info("[LOGIN] Request received for email={}", request.email());

        AuthLoginRequest authRequest = new AuthLoginRequest(request.email(), request.password());

        log.info("[LOGIN] Calling Auth Service endpoint={}", authServiceUrl + "/auth/api/v1/login");

        return webClient
                .post()
                  .uri(authServiceUrl + "/auth/api/v1/login")
                .header("clientName", authServiceClientName)
                .header(
                        "clientSecret",
                        authServiceClientSecret)

                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .doOnSuccess(response -> log.info("[REGISTER] RAW RESPONSE={}", response))
                .doOnError(error -> log.error("[LOGIN] Failed reason={}", error.getMessage(), error));
    }
}