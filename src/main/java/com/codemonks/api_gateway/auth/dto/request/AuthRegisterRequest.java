package com.codemonks.api_gateway.auth.dto.request;

import java.util.List;

public record AuthRegisterRequest(

        // Auth Service expects field name "username"
        String username,
        String password,
        List<String> roles
)
{ }