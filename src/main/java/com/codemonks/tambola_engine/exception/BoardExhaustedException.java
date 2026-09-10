package com.codemonks.tambola_engine.exception;


import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

@Getter
public class BoardExhaustedException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode = TambolaErrorCodesEnum.BOARD_EXHAUSTED;

    public BoardExhaustedException(Long roomId) {
        super("All 90 numbers have already been called for room " + roomId);
    }
}