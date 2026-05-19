package com.codemonks.tic_tac_toe_game_engine.constant;

import lombok.Getter;

@Getter
public enum EngineErrorCodesEnum {

    INVALID_MOVE(4001,"Invalid move: Cell is occupied or out of bounds."),
    INVALID_TURN(4002, "Wait for your turn! It's not your move."),
    PLAYER_NOT_FOUND(4003, "Player not found in game!");

   private final int code;
   private final String message;

   EngineErrorCodesEnum(int code,String message){
       this.code=code;
       this.message=message;
   }
}
