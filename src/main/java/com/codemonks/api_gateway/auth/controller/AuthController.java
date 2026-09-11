    package com.codemonks.api_gateway.auth.controller;

    import com.codemonks.api_gateway.auth.dto.request.*;
    import com.codemonks.api_gateway.auth.dto.response.*;
    import com.codemonks.api_gateway.auth.service.AuthGatewayService;

    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;

    import org.springframework.web.bind.annotation.*;

    import reactor.core.publisher.Mono;

    @RestController
    @RequestMapping("/game-gateway/auth/api/v1/")
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

        @PostMapping("/forgot-password")
        public Mono<SendOtpResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
            return authGatewayService.forgotPassword(request);
        }

        @PostMapping("/forgot-password/verify-otp")
        public Mono<VerifyForgotPasswordOtpResponse> verifyForgotPasswordOtp(@Valid @RequestBody VerifyForgotPasswordOtpRequest request) {
            return authGatewayService.verifyForgotPasswordOtp(request);
        }

        @PostMapping("/forgot-password/reset")
        public Mono<ChangePasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
            return authGatewayService.resetPassword(request);
        }
    }