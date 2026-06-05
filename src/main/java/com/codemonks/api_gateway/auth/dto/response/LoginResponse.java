package com.codemonks.api_gateway.auth.dto.response;


import java.util.List;
import java.util.Map;

public record LoginResponse(

        Long userId,
        String username,
        List<String> roles,
        List<String> permissions,
        String token,
        Map<String, Object> userProfile

) { }