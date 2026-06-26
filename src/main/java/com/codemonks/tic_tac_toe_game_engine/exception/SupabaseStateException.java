package com.codemonks.tic_tac_toe_game_engine.exception;

public class SupabaseStateException extends RuntimeException {

    public SupabaseStateException(String message) {
        super(message);
    }

    public SupabaseStateException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}