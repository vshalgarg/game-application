package com.codemonks.tambola_engine.exception;




public class BoardExhaustedException extends RuntimeException {
    public BoardExhaustedException(Long roomId) {
        super("All 90 numbers have already been called for room " + roomId);
    }
}