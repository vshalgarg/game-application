package com.codemonks.gameservice.service;

import com.codemonks.gameengine.dto.responseDto.GameStateResponse;
import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;

public interface GameService {

    void startGame(Long roomId);
    GameStateResponse makeMove(Long roomId, MakeMoveRequestDTO request);
}
