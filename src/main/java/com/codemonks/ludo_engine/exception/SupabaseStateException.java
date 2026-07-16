package com.codemonks.ludo_engine.exception;

public class SupabaseStateException extends RuntimeException {

    public SupabaseStateException(String message) {
        super(message);
    }

    public SupabaseStateException(String message, Throwable cause) {
        super(message, cause);
    }

}
