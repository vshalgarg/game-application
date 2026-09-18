package com.codemonks.api_gateway.auth.dto.request;

import lombok.Data;

@Data
public class SocialLoginRequest {

        private String provider;

        private String credential;
}