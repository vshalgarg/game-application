    package com.codemonks.api_gateway.auth.controller;

    import com.codemonks.api_gateway.auth.dto.request.LoginRequest;
    import com.codemonks.api_gateway.auth.dto.request.RegisterRequest;
    import com.codemonks.api_gateway.auth.dto.request.SocialLoginRequest;
    import com.codemonks.api_gateway.auth.dto.response.LoginResponse;
    import com.codemonks.api_gateway.auth.dto.response.RegisterResponse;
    import com.codemonks.api_gateway.auth.service.AuthGatewayService;

    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;

    import org.springframework.web.bind.annotation.*;

    import reactor.core.publisher.Mono;

    @RestController
    @RequestMapping("/game-gateway/auth/api/v1")
    @RequiredArgsConstructor
    public class AuthController {

        private final AuthGatewayService authGatewayService;

        @PostMapping("/register")
        public Mono<RegisterResponse> register(
                @Valid @RequestBody RegisterRequest request) {

            return authGatewayService.register(request);
        }

        @PostMapping("/login")
        public Mono<LoginResponse> login(
                @Valid @RequestBody LoginRequest request) {

            return authGatewayService.login(request);
        }

        @PostMapping("/login/social")
        public Mono<LoginResponse> socialLogin(
                @Valid @RequestBody SocialLoginRequest request
        ) {
            return authGatewayService.socialLogin(request);
        }
    }