package com.codemonks.api_gateway.auth.dto.request;


public record AuthLoginRequest(
        String username,
        String password
) { }