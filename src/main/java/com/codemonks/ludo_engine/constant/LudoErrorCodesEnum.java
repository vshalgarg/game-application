package com.codemonks.ludo_engine.constant;


import lombok.Getter;

@Getter
public enum LudoErrorCodesEnum {

    // Generic
    INVALID_REQUEST(4000, "Invalid request."),
    INVALID_MOVE(4001, "Invalid move."),
    INVALID_TURN(4002, "Wait for your turn! It's not your move."),
    PLAYER_NOT_FOUND(4003, "Player not found in game."),

    // Room / Game
    GAME_NOT_FOUND(4004, "Game not found."),
    ROOM_NOT_FOUND(4005, "Room not found."),
    GAME_ALREADY_STARTED(4006, "Game has already started."),
    GAME_NOT_STARTED(4007, "Game has not started yet."),
    GAME_ALREADY_FINISHED(4008, "Game has already finished."),
    GAME_IN_PROGRESS(4009, "Game is already in progress."),
    GAME_FULL(4010, "Game is full. No more players can join."),

    // Player
    PLAYER_ALREADY_JOINED(4011, "Player has already joined the game."),
    PLAYER_ALREADY_LEFT(4012, "Player has already left the game."),
    PLAYER_NOT_ACTIVE(4013, "Player is not active."),
    PLAYER_ELIMINATED(4014, "Player has been eliminated."),
    HOST_ONLY_ACTION(4015, "Only the host can perform this action."),

    // Dice
    DICE_NOT_ROLLED(4016, "Roll the dice before making a move."),
    DICE_ALREADY_ROLLED(4017, "Dice has already been rolled for this turn."),
    INVALID_DICE_VALUE_CONSUMPTION(4018, "Invalid dice value."),

    // Token
    TOKEN_NOT_FOUND(4019, "Token not found."),
    TOKEN_NOT_AT_HOME(4020, "Token is already out of home."),
    TOKEN_AT_HOME(4021, "Token is still at home."),
    TOKEN_CANNOT_MOVE(4022, "Selected token cannot be moved."),
    TOKEN_ALREADY_FINISHED(4023, "Token has already reached the destination."),
    TOKEN_NOT_OWNED(4024, "Selected token does not belong to the player."),
    NO_MOVABLE_TOKEN(4025, "No movable token available for the current dice roll."),
    TOKEN_BLOCKED(4026, "Token is blocked and cannot move."),

    // Ludo Rules
    SIX_REQUIRED_TO_EXIT_HOME(4027, "A six is required to move a token out of home."),
    MOVE_EXCEEDS_DESTINATION(4028, "Move exceeds the destination."),
    EXTRA_TURN_NOT_ALLOWED(4029, "Extra turn is not allowed."),
    TURN_ALREADY_COMPLETED(4030, "Current turn has already been completed."),
    INVALID_TOKEN_SELECTION(4031, "Invalid token selected."),
    SAFE_ZONE_MOVE_NOT_ALLOWED(4032, "Move is not allowed in the safe zone."),
    CAPTURE_NOT_ALLOWED(4033, "Token cannot be captured in a safe zone."),

    // Match State
    WAITING_FOR_PLAYERS(4034, "Waiting for more players to join."),
    INSUFFICIENT_PLAYERS(4035, "Not enough players to start the game."),
    WINNER_ALREADY_DECLARED(4036, "Winner has already been declared."),
    MATCH_CANCELLED(4037, "Match has been cancelled."),
    MATCH_EXPIRED(4038, "Match has expired."),


    NO_PENDING_DICE(4039, "No pending dice available."),
    INVALID_PATH_INDEX(4039, "this is not right  available path."),

    // Internal
    INTERNAL_ERROR(5000, "Internal server error.");

    private final int code;
    private final String message;

    LudoErrorCodesEnum(int code, String message){
        this.code=code;
        this.message=message;
    }
}
