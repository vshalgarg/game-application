package com.codemonks.tic_tac_toe_game_engine.exception;

import com.codemonks.tic_tac_toe_game_engine.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Component("ticTacToeGlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TicTacToeEngineException.class)
    public ResponseEntity<ErrorResponse>handleEngineException(TicTacToeEngineException exception){
        ErrorResponse errorResponse=ErrorResponse.builder()
                .errorCode(exception.getCode())
                .errorMessage(exception.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
