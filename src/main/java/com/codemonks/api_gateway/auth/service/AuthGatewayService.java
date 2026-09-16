package com.codemonks.api_gateway.auth.service;

import com.codemonks.api_gateway.auth.dto.request.AuthLoginRequest;
import com.codemonks.api_gateway.auth.dto.request.AuthRegisterRequest;
import com.codemonks.api_gateway.auth.dto.request.LoginRequest;
import com.codemonks.api_gateway.auth.dto.request.RegisterRequest;
import com.codemonks.api_gateway.auth.dto.response.CountryResponse;
import com.codemonks.api_gateway.auth.dto.response.LoginResponse;
import com.codemonks.api_gateway.auth.dto.response.RegisterResponse;
import com.codemonks.api_gateway.util.ResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthGatewayService {

    private final WebClient webClient;
    private final ResponseParser responseParser;

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
                .bodyToMono(String.class)
                .flatMap(body -> responseParser.parseResponse(body, RegisterResponse.class))
                .doOnSuccess(response -> log.info("[REGISTER] Registration successful"))
                .doOnError(error -> log.error("[REGISTER] {}", error.getMessage(), error));
    }

    public Mono<LoginResponse> login(LoginRequest request) {
        log.info("[LOGIN] Request received for email={}", request.email());
        AuthLoginRequest authRequest = new AuthLoginRequest(request.email(), request.password());
        log.info("[LOGIN] Calling Auth Service endpoint={}", authServiceUrl + "/auth/api/v1/login");
        return webClient
                .post()
                .uri(authServiceUrl + "/auth/api/v1/login")
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> responseParser.parseResponse(body, LoginResponse.class))
                .doOnSuccess(response -> log.info("[LOGIN] Login successful"))
                .doOnError(error -> log.error("[LOGIN] {}", error.getMessage(), error));
    }

    public Mono<String> getAllCountries() {
        log.info("[COUNTRIES] Fetching all countries");
        String url = authServiceUrl + "/auth/api/v1/countries";
        log.info("[COUNTRIES] Calling Auth Service endpoint={}", url);
        return webClient
                .get()
                .uri(url)
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.info("[COUNTRIES] Auth API response: {}", body))
                .doOnError(error -> log.error("[COUNTRIES] Auth API error", error));
    }

    public Mono<CountryResponse> getCountryByName(String name) {
        log.info("[COUNTRY] Fetching country details for name={}", name);
        String url = authServiceUrl + "/auth/api/v1/countries/" + name;
        log.info("[COUNTRY] Calling Auth Service endpoint={}", url);
        return webClient
                .get()
                .uri(url)
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> responseParser.parseResponse(body, CountryResponse.class))
                .doOnSuccess(response -> log.info("[COUNTRY] Country fetched successfully: {}", name))
                .doOnError(error -> log.error("[COUNTRY] {}", error.getMessage(), error));
    }
}