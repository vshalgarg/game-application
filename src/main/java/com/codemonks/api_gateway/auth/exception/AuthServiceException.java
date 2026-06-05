package com.codemonks.api_gateway.auth.exception;


public class AuthServiceException extends RuntimeException {

    public AuthServiceException(String message) {
        super(message);
    }
}