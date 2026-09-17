package com.codemonks.api_gateway.auth.service;

import com.codemonks.api_gateway.auth.dto.request.*;
import com.codemonks.api_gateway.auth.dto.response.*;
import com.codemonks.api_gateway.util.ResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthGatewayService {

    private final WebClient webClient;
    private final ResponseParser responseParser;
    private final GameProfileGatewayService gameProfileGatewayService;
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

    public Mono<LoginResponse> socialLogin(SocialLoginRequest request) {
        log.info("[SOCIAL LOGIN] Request received for provider={}", request.getProvider());
        String endpoint = "/auth/api/v1/login/social";
        return webClient
                .post()
                .uri(authServiceUrl + endpoint)
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> responseParser.parseResponse(body, LoginResponse.class))
                .flatMap(loginResponse -> {
                    Map<String, Object> profile = loginResponse.userProfile();
                    String name = profile != null && profile.get("name") != null
                            ? profile.get("name").toString()
                            : null;
                    String email = profile != null && profile.get("email") != null
                            ? profile.get("email").toString()
                            : loginResponse.username();
                    String dob = profile != null && profile.get("dob") != null
                            ? profile.get("dob").toString()
                            : null;
                    ProfileRequest profileRequest = ProfileRequest.builder()
                                    .userId(loginResponse.userId())
                                    .name(name)
                                    .email(email)
                                    .dob(dob)
                                    .build();
                    return gameProfileGatewayService
                            .createOrUpdateProfile(profileRequest)
                            .thenReturn(loginResponse);
                })
                .doOnNext(body -> log.info("[SOCIAL LOGIN] AUTH API RESPONSE = {}", body))
                .doOnSuccess(response -> log.info("[SOCIAL LOGIN] Login successful"))
                .doOnError(error ->
                        log.error(
                                "[SOCIAL LOGIN] Failed for provider={}: {}",
                                request.getProvider(),
                                error.getMessage(),
                                error
                        )
                );
    }

    public Mono<SendOtpResponse> forgotPassword(ForgotPasswordRequest request) {
        log.info("[FORGOT PASSWORD] Request received for email={}", request.email());
        AuthForgotPasswordRequest authRequest = new AuthForgotPasswordRequest(request.email());
        log.info("[FORGOT PASSWORD] Calling Auth Service endpoint={}", authServiceUrl + "/auth/api/v1/forgot-password");
        return webClient
                .post()
                .uri(authServiceUrl + "/auth/api/v1/forgot-password")
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.info("[FORGOT PASSWORD] Auth API raw response={}", body))
                .flatMap(body -> responseParser.parseResponse(body, SendOtpResponse.class))
                .doOnSuccess(response -> log.info("[FORGOT PASSWORD] OTP request successful"))
                .doOnError(error -> log.error("[FORGOT PASSWORD] {}", error.getMessage(), error));
    }

    public Mono<VerifyForgotPasswordOtpResponse> verifyForgotPasswordOtp(VerifyForgotPasswordOtpRequest request) {
        log.info("[VERIFY OTP] Request received for email={}", request.email());
        AuthVerifyForgotPasswordOtpRequest authRequest = new AuthVerifyForgotPasswordOtpRequest(request.email(), request.verificationCode());
        log.info("[VERIFY OTP] Calling Auth Service endpoint={}", authServiceUrl + "/auth/api/v1/forgot-password/verify-otp");
        return webClient
                .post()
                .uri(authServiceUrl + "/auth/api/v1/forgot-password/verify-otp")
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> responseParser.parseResponse(body, VerifyForgotPasswordOtpResponse.class))
                .doOnSuccess(response -> log.info("[VERIFY OTP] OTP verification successful"))
                .doOnError(error -> log.error("[VERIFY OTP] {}", error.getMessage(), error));
    }

    public Mono<ChangePasswordResponse> resetPassword(ResetPasswordRequest request) {
        return webClient.post()
                .uri(authServiceUrl + "/auth/api/v1/forgot-password/reset")
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChangePasswordResponse.class);
    }

    public Mono<SendOtpResponse> resendForgotPasswordOtp(ForgotPasswordRequest request) {
        log.info("[RESEND OTP] Request received for email={}", request.email());
        AuthForgotPasswordRequest authRequest = new AuthForgotPasswordRequest(request.email());
        String url = authServiceUrl + "/auth/api/v1/forgot-password/resend-otp";
        log.info("[RESEND OTP] Calling Auth Service endpoint={}", url);
        return webClient
                .post()
                .uri(url)
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.info("[RESEND OTP] Auth API raw response={}",body))
                .flatMap(body -> responseParser.parseResponse(body, SendOtpResponse.class))
                .doOnSuccess(response -> log.info("[RESEND OTP] OTP resent successfully"))
                .doOnError(error -> log.error("[RESEND OTP] {}", error.getMessage(), error)
                );
    }

     public Mono<ApiResponse<List<CountryResponse>>> getAllCountries() {
        log.info("[COUNTRIES] Fetching all countries");
        String url = authServiceUrl + "/auth/api/v1/countries";
        log.info("[COUNTRIES] Calling Auth Service endpoint={}", url);
        return webClient
                .get()
                .uri(url)
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .retrieve()
                .bodyToFlux(CountryResponse.class)
                .collectList()
                .map(ApiResponse::success)
                .doOnSuccess(response -> log.info("[COUNTRIES] Countries fetched successfully"))
                .doOnError(error -> log.error("[COUNTRIES] Auth API error", error));
    }

    public Mono<ApiResponse<CountryResponse>> getCountryByName(String name) {
        log.info("[COUNTRY] Fetching country details for name={}", name);
        String url = authServiceUrl + "/auth/api/v1/countries/" + name;
        log.info("[COUNTRY] Calling Auth Service endpoint={}", url);
        return webClient
                .get()
                .uri(url)
                .header("clientName", authServiceClientName)
                .header("clientSecret", authServiceClientSecret)
                .retrieve()
                .bodyToMono(CountryResponse.class)
                .map(ApiResponse::success)
                .doOnSuccess(response ->log.info("[COUNTRY] Country fetched successfully: {}", name))
                .doOnError(error -> log.error("[COUNTRY] {}", error.getMessage(), error));
    }
}