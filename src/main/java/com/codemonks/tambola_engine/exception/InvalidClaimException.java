package com.codemonks.tambola_engine.exception;


import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

// Jab koi claim reject ho - rule already claimed, pattern match nahi
// hua, ya invalid ticket/player. Enum khud carry karta hai taaki
// GlobalExceptionHandler seedha errorCode nikaal sake (Ludo ke
// InvalidMoveException pattern jaisa hi).
@Getter
public class InvalidClaimException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode;

    public InvalidClaimException(TambolaErrorCodesEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // Custom message ke saath bhi bhej sakein (jaise "Rule FULL_HOUSE
    // already claimed" - dynamic detail ke saath), enum sirf code ke liye.
    public InvalidClaimException(TambolaErrorCodesEnum errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}