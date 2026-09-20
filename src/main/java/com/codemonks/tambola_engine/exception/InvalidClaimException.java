package com.codemonks.tambola_engine.exception;


import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

@Getter
public class InvalidClaimException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode;

    public InvalidClaimException(TambolaErrorCodesEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InvalidClaimException(TambolaErrorCodesEnum errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}