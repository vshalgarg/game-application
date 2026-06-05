package com.codemonks.api_gateway.auth.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Handles auth service related exceptions

    @ExceptionHandler(AuthServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, String> handleAuthServiceException(AuthServiceException authServiceException) {

        log.error("Auth Service Error : {}", authServiceException.getMessage());

        return Map.of("message", authServiceException.getMessage());
    }

     // Fallback exception handler
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception exception) {
        log.error("Unexpected Error : ", exception);
        return Map.of("message", "Something went wrong");
    }
}