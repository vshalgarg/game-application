package com.codemonks.ludo_game_engine.exception;


public class InvalidMoveException extends RuntimeException {

    public InvalidMoveException(String message){
        super(message);
    }
}
