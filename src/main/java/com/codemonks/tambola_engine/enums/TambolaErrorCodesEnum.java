package com.codemonks.tambola_engine.enums;


import lombok.Getter;

@Getter
public enum TambolaErrorCodesEnum {

    // Generic
    INVALID_REQUEST(4000, "Invalid request."),

    // Room / Game
    ROOM_NOT_FOUND(4001, "Room not found or not currently active."),
    GAME_NOT_RUNNING(4002, "Game is not currently running."),
    BOARD_EXHAUSTED(4003, "All 90 numbers have already been called."),

    // Claim
    RULE_NOT_CONFIGURED(4010, "This claim type is not configured for this game."),
    RULE_ALREADY_CLAIMED(4011, "This claim type has already been won."),
    INVALID_CLAIM_PATTERN(4012, "Ticket does not satisfy the claimed pattern."),
    PLAYER_HAS_NO_TICKETS(4013, "Player has no tickets in this room."),
    TICKET_NOT_OWNED(4014, "Ticket does not belong to this player."),

    // NAYA: jab rule ke saare winner-slots already fill ho chuke hon
    // (jaise limit 5 thi, 5 log jeet chuke, 6th player claim kare)
    RULE_SLOTS_FULL(4015, "Sorry, all winner-slots for this rule are already taken."),

    // Ticket generation
    TICKET_GENERATION_FAILED(4020, "Failed to generate a valid ticket layout."),

    // External / Supabase
    SUPABASE_STATE_ERROR(5001, "Failed to sync game state with Supabase."),

    // Internal
    INTERNAL_ERROR(5000, "Internal server error.");

    private final int code;
    private final String message;

    TambolaErrorCodesEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}