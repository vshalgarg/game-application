package com.codemonks.api_gateway.exception;

import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {

    private final ErrorResponse errorResponse;
    public GatewayException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
}
