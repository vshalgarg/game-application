package com.codemonks.api_gateway.auth.dto.response;

public record VerifyForgotPasswordOtpResponse(
        Boolean verified,
        String resetToken,
        String message
) {
}