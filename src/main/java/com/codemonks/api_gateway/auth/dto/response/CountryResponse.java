package com.codemonks.api_gateway.auth.dto.response;

public record CountryResponse(
        String code,
        String name,
        String dialCode
) {
}