package com.codemonks.gameservice.constants;

public class ApiUrlConstants {

    private ApiUrlConstants() {}

    public static final String BASE_API = "api/v1";
    public static class Room{
        public static final String BASE = BASE_API + "/rooms";
        public static final String CREATE_ROOM = "/create";
        public static final String JOIN_ROOM = "/{roomCode}/join";
        public static final String START_GAME = "/{roomCode}/start";
        public static final String RESTART_GAME = "/{roomCode}/restart";
        public static final String GET_ROOM_DETAILS = "/{roomCode}";
        public static final String ROLL_DICE = "/{roomCode}/roll-dice";
    }

    public static class Game{
        public static final String BASE = BASE_API+ "/game";
        public static final String MAKE_MOVE = "/{roomCode}/move";
    }
}
