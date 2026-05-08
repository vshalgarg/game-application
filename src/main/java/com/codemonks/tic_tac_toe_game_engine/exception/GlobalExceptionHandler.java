package com.codemonks.tic_tac_toe_game_engine.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TicTacToeEngineException.class)
    public ResponseEntity<?>handleEngineException(TicTacToeEngineException exception){
        return ResponseEntity.badRequest().body(Map.of("errorCode",exception.getCode(),
                        "errorMessage",exception.getMessage()));

    }
}
