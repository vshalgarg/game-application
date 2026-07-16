package com.codemonks.ludo_engine.exception;

import com.codemonks.ludo_engine.constant.ErrorCodesEnum;
import lombok.Getter;

@Getter
public class GameRuleException  extends RuntimeException{

    private final ErrorCodesEnum errorCodesEnum;
    public GameRuleException(ErrorCodesEnum errorCodesEnum){
        super(errorCodesEnum.getMessage());
        this.errorCodesEnum = errorCodesEnum;
    }
}
