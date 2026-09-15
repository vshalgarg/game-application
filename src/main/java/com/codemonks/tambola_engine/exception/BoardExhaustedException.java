package com.codemonks.tambola_engine.exception;

import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

@Getter
public class BoardExhaustedException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode = TambolaErrorCodesEnum.BOARD_EXHAUSTED;

    // NAYA — no-arg overload. NumberGeneratorServiceImpl ab pure/stateless
    // hai (roomId iske paas hota hi nahi, sirf calledNumbers milta hai),
    // isliye generic message ke saath throw karta hai. roomId-context
    // TimerServiceImpl ke paas already hai (jahan se ye exception catch
    // hoti hai aur room ko FINISHED mark kiya jaata hai) — us log-line
    // me roomId already print ho raha hai, isliye yahan dobara zaroori nahi.
    public BoardExhaustedException() {
        super("All 90 numbers have already been called");
    }

    public BoardExhaustedException(Long roomId) {
        super("All 90 numbers have already been called for room " + roomId);
    }
}