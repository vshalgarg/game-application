package com.codemonks.gameengine.contract;

import com.codemonks.gameengine.dto.requestDto.MoveRequest;
import com.codemonks.gameengine.dto.requestDto.StartGameRequest;
import com.codemonks.gameengine.dto.responseDto.GameStateResponse;

public interface TicTacToeEngine {

    GameStateResponse startGame(StartGameRequest request);
    GameStateResponse makeMove(GameStateResponse currentState, MoveRequest moveRequest);
}
