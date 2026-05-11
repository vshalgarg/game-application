package com.codemonks.gameservice.constants;


import lombok.Getter;

@Getter
public enum ResponseErrorCodes {

    INTERNAL_SERVER_ERROR(500, "Something went wrong"),
    EXTERNAL_SERVICE_ERROR(2001, "External service error"),
    INVALID_REQUEST(4000, "Invalid request"),

    // Game/Room specific
    ROOM_FULL(1001, "Room is already full"),
    ROOM_NOT_FOUND(1002, "Room not found"),
    NOT_YOUR_TURN (1003, "Not your turn"),
    GAME_CONFIG_NOT_FOUND(1004, "Game config not found"),
    USER_NOT_FOUND(1005, "User not found"),
    ONLY_HOST_CAN_START_GAME(1006, "Only host can start the game"),
    GAME_ALREADY_STARTED(1007, "Game already started"),
    USER_ALREADY_IN_ROOM(1008, "User_Already_In_Room"),
    ROOM_NOT_FULL(1009, "Room is not full"),
    GAME_NOT_FOUND(1010, "Room not found"),
    GAME_ENGINE_NOT_FOUND(1011, "Game engine not found");

    private final int code;
    private final String message;

    ResponseErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
