package com.codemonks.gameservice.constants;


import lombok.Getter;

@Getter
public enum ResponseErrorCodes {

    INTERNAL_SERVER_ERROR(5000, "Something went wrong"),
    INVALID_REQUEST(4000, "Invalid request"),

    // Game/Room specific
    ROOM_FULL(1001, "Room is already full"),
    ROOM_NOT_FOUND(1002, "Room not found"),
    NOT_YOUR_TURN (1003, "Not your turn"),
    GAME_CONFIG_NOT_FOUND(1004, "Game config not found");

    private final int code;
    private final String message;

    ResponseErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
