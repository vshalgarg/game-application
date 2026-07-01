package com.codemonks.api_gateway.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<ErrorResponse> handleGatewayException(GatewayException ex) {
        return ResponseEntity.ok(ex.getErrorResponse());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(WebExchangeBindException ex) {

        log.warn("Validation failed", ex);
        String message = ex.getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        ErrorResponse response = new ErrorResponse(
                message,
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(response);
    }
}