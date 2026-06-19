package com.codemonks.gameservice.service;


import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.entity.RoomEntity;

public interface GameService {

    EngineGameStateResponseDTO startGame(RoomEntity room);
    EngineGameStateResponseDTO makeMove(String roomCode, MakeMoveRequestDTO request);

}
