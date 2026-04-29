package com.codemonks.gameservice.constants;

import lombok.Getter;

@Getter
public enum ResponseErrorCodes {

    INTERNAL_SERVER_ERROR(5000, "Something went wrong"),
    INVALID_REQUEST(4000, "Invalid request"),

    // Game/Room specific
    ROOM_FULL(1001, "Room is already full"),
    ROOM_NOT_FOUND(1002, "Room not found"),
    PLAYER_NOT_FOUND(1003, "Player not found"),
    INVALID_MOVE(1004, "Invalid move");

    private final int code;
    private final String message;

    ResponseErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
