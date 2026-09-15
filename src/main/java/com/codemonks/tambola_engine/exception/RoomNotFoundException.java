package com.codemonks.tambola_engine.exception;

import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

// Jab TambolaGameStateRepository.findById() diye gaye roomId se koi
// row nahi dhoondh paati (room khatam ho chuka, ya galat roomId).
// Dedicated exception - IllegalStateException generic hai aur
// GlobalExceptionHandler me genuine-unexpected-bugs ke liye reserved
// rehna chahiye (handleGenericException() catch karega unhe).
@Getter
public class RoomNotFoundException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode = TambolaErrorCodesEnum.ROOM_NOT_FOUND;

    public RoomNotFoundException(Long roomId) {
        super("No active room found for roomId=" + roomId);
    }
}