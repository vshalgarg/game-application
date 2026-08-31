package com.codemonks.tambola_engine.exception;



/**
 * Thrown when NumberGeneratorService is asked to generate a new
 * number for a room, but all 90 numbers have already been called.
 * <p>
 * When this happens, the caller (TimerService) should stop calling
 * numbers for that room and move the game toward FINISHED, since
 * there is nothing left to draw.
 */
public class BoardExhaustedException extends RuntimeException {
    public BoardExhaustedException(Long roomId) {
        super("All 90 numbers have already been called for room " + roomId);
    }
}