package com.codemonks.tambola_engine.exception;


import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

// Jab TicketGenerator ek valid ticket-layout generate nahi kar paata
// (constraint-satisfaction fail ho jaaye, jo practically bahut rare
// hai - dekho TicketGenerator.assignRowsToColumns() ka comment).
// Pehle iski jagah generic IllegalStateException use ho raha tha,
// jo GlobalExceptionHandler me ROOM_NOT_FOUND ke saath clash karta
// tha (dono same exception-type share kar rahe the) - isi wajah se
// ye dedicated exception banayi, taaki dono cases alag-alag
// error-code/message client ko dein.
@Getter
public class TicketPersistenceException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode = TambolaErrorCodesEnum.TICKET_GENERATION_FAILED;

    public TicketPersistenceException(String message) {
        super(message);
    }
}