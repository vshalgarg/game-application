package com.codemonks.api_gateway.auth.dto.request;


public record AuthLoginRequest(

        // Auth Service expects username
        String username,
        String password
) { }