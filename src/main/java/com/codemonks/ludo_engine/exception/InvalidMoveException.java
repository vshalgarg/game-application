package com.codemonks.ludo_engine.exception;


import com.codemonks.ludo_engine.constant.ErrorCodesEnum;
import lombok.Getter;

@Getter
public class InvalidMoveException extends RuntimeException {

    private final ErrorCodesEnum errorCodesEnum;

    public InvalidMoveException(ErrorCodesEnum errorCodesEnum){
        super(errorCodesEnum.getMessage());
        this.errorCodesEnum = errorCodesEnum;
    }
}
