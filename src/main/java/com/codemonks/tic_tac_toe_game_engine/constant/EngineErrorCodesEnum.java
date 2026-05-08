package com.codemonks.tic_tac_toe_game_engine.constant;

import lombok.Getter;

@Getter
public enum EngineErrorCodesEnum {

   INVALID_MOVE(4001,"Invalid move: Cell is occupied or out of bounds."),
   GAME_ALREADY_OVER(4002,"Game is already finished.");

   private final int code;
   private final String message;

   EngineErrorCodesEnum(int code,String message){
       this.code=code;
       this.message=message;
   }
}
