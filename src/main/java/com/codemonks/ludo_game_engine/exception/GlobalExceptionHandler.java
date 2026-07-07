package com.codemonks.ludo_game_engine.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidMoveException.class)
    public ResponseEntity<Map<String, String>> handleInvalidMove(InvalidMoveException exception) {
        log.warn("[INVALID_MOVE] {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "INVALID_MOVE", "message", exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException exception) {
        log.warn("[GAME_STATE_CONFLICT] {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "CONFLICT", "message", exception.getMessage()));
    }

    @ExceptionHandler(GameRuleException.class)
    public ResponseEntity<Map<String, String>> handleGameRule(GameRuleException exception) {
        log.warn("[GAME_RULE_VIOLATION] {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "GAME_RULE_VIOLATION", "message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        String error = Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage();
        log.error("Validation failed:{}", error);
        return ResponseEntity.badRequest()
                .body(Map.of("error", "VALIDATION_ERROR", "message", error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception exception) {
        log.error("Unexpected exception:{}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "INTERNAL_ERROR", "message", "Internal server error"));
    }
}