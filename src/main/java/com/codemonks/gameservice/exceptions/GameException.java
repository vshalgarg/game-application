package com.codemonks.gameservice.exceptions;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import lombok.Getter;

@Getter
public class GameException extends RuntimeException{
    private final ResponseErrorCodes errorCode;

    public GameException(ResponseErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GameException(ResponseErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
