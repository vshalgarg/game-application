package com.codemonks.gameservice.service;


import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.dto.response.GameStateResponse;

public interface GameService {

    void startGame(Long roomId);
    GameStateResponse makeMove(Long roomId, MakeMoveRequestDTO request);
}
