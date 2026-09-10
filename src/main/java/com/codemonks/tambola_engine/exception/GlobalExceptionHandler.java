package com.codemonks.tambola_engine.exception;

import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Named bean - Ludo ke GlobalExceptionHandler se clash na ho isliye
// (poora project ek hi Spring context me chalta hai).
@RestControllerAdvice
@Component("tambolaGlobalExceptionHandler")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidClaimException.class)
    public ResponseEntity<ErrorResponse> handleInvalidClaim(InvalidClaimException exception) {
        log.warn("[INVALID_CLAIM] {}", exception.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(exception.getErrorCode().getCode())
                .errorMessage(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BoardExhaustedException.class)
    public ResponseEntity<ErrorResponse> handleBoardExhausted(BoardExhaustedException exception) {
        log.warn("[BOARD_EXHAUSTED] {}", exception.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(exception.getErrorCode().getCode())
                .errorMessage(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(SupabaseStateException.class)
    public ResponseEntity<ErrorResponse> handleSupabaseState(SupabaseStateException exception) {
        log.error("[SUPABASE_STATE_ERROR] {}", exception.getMessage(), exception);
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(exception.getErrorCode().getCode())
                .errorMessage(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException exception) {
        // GameStateRegistry.get() isi exception ko throw karta hai jab
        // roomId ki koi active state nahi milti.
        log.warn("[ROOM_NOT_FOUND] {}", exception.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(TambolaErrorCodesEnum.ROOM_NOT_FOUND.getCode())
                .errorMessage(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    @ExceptionHandler(TicketPersistenceException.class)
    public ResponseEntity<ErrorResponse> handleTicketPersistence(TicketPersistenceException exception) {
        log.error("[TICKET_GENERATION_FAILED] {}", exception.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(exception.getErrorCode().getCode())
                .errorMessage(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}