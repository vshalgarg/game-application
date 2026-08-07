package com.codemonks.gameservice.constants;


public final class BotConstants {

    private BotConstants() {}

    public static final long FIRST_BOT_USER_ID = -1L;

    public static long getBotUserId(int botIndex) {
        return FIRST_BOT_USER_ID - botIndex;
    }
}