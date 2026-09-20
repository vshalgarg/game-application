package com.codemonks.tambola_engine.exception;

import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import lombok.Getter;

@Getter
public class RoomNotFoundException extends RuntimeException {

    private final TambolaErrorCodesEnum errorCode = TambolaErrorCodesEnum.ROOM_NOT_FOUND;

    public RoomNotFoundException(Long roomId) {
        super("No active room found for roomId=" + roomId);
    }
}