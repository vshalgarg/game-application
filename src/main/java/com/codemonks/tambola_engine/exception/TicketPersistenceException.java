package com.codemonks.tambola_engine.exception;


import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

@Getter
public class TicketPersistenceException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode = TambolaErrorCodesEnum.TICKET_GENERATION_FAILED;

    public TicketPersistenceException(String message) {
        super(message);
    }
}