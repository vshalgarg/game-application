package com.codemonks.ludo_engine.exception;


import com.codemonks.ludo_engine.constant.LudoErrorCodesEnum;
import lombok.Getter;

@Getter
public class InvalidMoveException extends RuntimeException {

    private final LudoErrorCodesEnum LudoErrorCodesEnum;

    public InvalidMoveException(LudoErrorCodesEnum LudoErrorCodesEnum){
        super(LudoErrorCodesEnum.getMessage());
        this.LudoErrorCodesEnum = LudoErrorCodesEnum;
    }
}
