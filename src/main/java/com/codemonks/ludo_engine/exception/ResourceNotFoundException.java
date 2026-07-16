package com.codemonks.ludo_engine.exception;

import com.codemonks.ludo_engine.constant.ErrorCodesEnum;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCodesEnum errorCode;

    public ResourceNotFoundException(ErrorCodesEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}