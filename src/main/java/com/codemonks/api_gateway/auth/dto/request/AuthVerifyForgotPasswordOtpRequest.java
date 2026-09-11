package com.codemonks.api_gateway.auth.dto.request;

public record AuthVerifyForgotPasswordOtpRequest(
        String username,
        String verificationCode
) {
}