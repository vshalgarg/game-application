package com.codemonks.tic_tac_toe_game_engine.exception;


import com.codemonks.tic_tac_toe_game_engine.constant.EngineErrorCodesEnum;
import lombok.Getter;

@Getter
public class TicTacToeEngineException extends RuntimeException{

    private final int code;

    public TicTacToeEngineException(EngineErrorCodesEnum errorCode){

        super(errorCode.getMessage());
        this.code=errorCode.getCode();
    }
}
