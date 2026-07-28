package com.codemonks.ludo_engine.exception;

import com.codemonks.ludo_engine.constant.LudoErrorCodesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Component("ludoGlobalExceptionHandler")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidMoveException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMove(InvalidMoveException exception) {
        log.warn("[INVALID_MOVE] {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getLudoErrorCodesEnum().getCode(),
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(GameRuleException.class)
    public ResponseEntity<ErrorResponse> handleGameRule(GameRuleException exception) {
        log.warn("[GAME_RULE_VIOLATION] {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getLudoErrorCodesEnum().getCode(),
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {

        FieldError fieldError = exception.getBindingResult().getFieldError();
        String validationMessage = fieldError != null
                ? fieldError.getDefaultMessage()
                : "Validation failed";

        log.error("Validation failed: {}", validationMessage);

        ErrorResponse response = ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(validationMessage)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {

        log.error("Unexpected exception", exception);

        ErrorResponse response = ErrorResponse.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Internal server error")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {

        LudoErrorCodesEnum error = exception.getErrorCode();

        log.error("Resource not found: {}", error.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(error.getCode())
                .errorMessage(error.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}