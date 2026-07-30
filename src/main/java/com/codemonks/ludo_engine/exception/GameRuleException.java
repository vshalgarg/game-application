package com.codemonks.ludo_engine.exception;

import com.codemonks.ludo_engine.constant.LudoErrorCodesEnum;
import lombok.Getter;

@Getter
public class GameRuleException  extends RuntimeException{

    private final LudoErrorCodesEnum LudoErrorCodesEnum;
    public GameRuleException(LudoErrorCodesEnum LudoErrorCodesEnum){
        super(LudoErrorCodesEnum.getMessage());
        this.LudoErrorCodesEnum = LudoErrorCodesEnum;
    }
}
