package com.codemonks.gameservice.exceptions;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import lombok.Getter;

@Getter
public class GameException extends RuntimeException{
    private final int code;

    public GameException(ResponseErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
