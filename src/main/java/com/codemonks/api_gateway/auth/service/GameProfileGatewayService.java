
package com.codemonks.api_gateway.auth.service;

import com.codemonks.api_gateway.auth.dto.request.ProfileRequest;
import com.codemonks.api_gateway.auth.dto.request.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameProfileGatewayService {

    private final WebClient webClient;

    @Value("${game.service.url}")
    private String gameServiceUrl;

    public Mono<Void> createOrUpdateProfile(ProfileRequest request) {
        return webClient
                .post()
                .uri(gameServiceUrl + "/game-service/api/v1/profile")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("[GAME PROFILE] Profile saved. userId={}", request.getUserId()))
                .then();
    }

    public Mono<String> getProfile() {
        return webClient
                .get()
                .uri(gameServiceUrl + "/game-service/api/v1/profile")
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("[GAME PROFILE] Profile fetched"));
    }
    public Mono<String> updateProfile(UpdateProfileRequest request) {
        return webClient
                .put()
                .uri(gameServiceUrl + "/game-service/api/v1/profile")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("[GAME PROFILE] Profile updated"));
    }
}
