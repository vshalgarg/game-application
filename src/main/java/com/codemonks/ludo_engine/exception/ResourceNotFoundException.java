package com.codemonks.ludo_engine.exception;

import com.codemonks.ludo_engine.constant.LudoErrorCodesEnum;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final LudoErrorCodesEnum errorCode;

    public ResourceNotFoundException(LudoErrorCodesEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}