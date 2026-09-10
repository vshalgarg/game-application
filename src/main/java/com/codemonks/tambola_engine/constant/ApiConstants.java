package com.codemonks.tambola_engine.constant;


// Utility class - saare API-path constants ek jagah, taaki controller aur
// (future me) koi bhi doosri jagah har baar string-literal na likhna pade.
// Private constructor - koi bhi galti se `new ApiConstants()` na kar sake.
public final class ApiConstants {

    private ApiConstants() {}

    public static final String CONTEXT_PATH = "/game-engine/tambola-engine";
    public static final String BASE_API = CONTEXT_PATH + "/api/v1";

    // Host "Start Game" dabata hai -> game-service isi endpoint ko call
    // karta hai -> GameSetupServiceImpl.initializeGame() trigger hota hai.
    public static final String START_GAME = "/start-game";

    // FUTURE endpoints (abhi implement nahi hue, but naam yahin reserve
    // kar rahe hain taaki Controller banate waqt pattern clear rahe):
    // - Claim submit karne ke liye (jab ClaimService banegi)
    // - Room-lobby publish karne ke liye (agar Tambola me bhi lobby-realtime
    //   Ludo jaisa concept ho)

    // NAYA: Player claim submit karta hai ("Early Five", "Full House",
    // etc.) -> ClaimServiceImpl.submitClaim()

    public static final String SUBMIT_CLAIM = "/claim";

}