package com.codemonks.gameservice.exceptions;

import com.codemonks.gameservice.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(
                        500,
                        "Something went wrong. Please try again."
                ));
    }

    @ExceptionHandler(GameException.class)
    public ResponseEntity<ApiResponse<Void>> handleGameException(GameException ex) {

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(
                        ex.getCode(),
                        ex.getMessage()
                ));
    }
}
