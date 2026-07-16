package com.codemonks.ludo_engine.constant;

public final class ApiConstants {

    private ApiConstants(){} //no one can make it object

    public static final String CONTEXT_PATH = "/game-engine/ludo-engine";
    public static final String BASE_API= CONTEXT_PATH + "/api/v1";
    public static final String START_GAME = "/start-game";
    public static final String ROLL_DICE="/roll-dice";
    public static final String PROCESS_MOVE="/make-move";
    public static final String LOBBY = "/lobby";


}
