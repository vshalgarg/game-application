package com.codemonks.gameservice.service;


import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.dto.request.JoinRoomRequestDTO;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.model.BoardLayout;


public interface RoomService {

    RoomResponseDTO createRoom(CreateRoomRequestDTO request);
    RoomResponseDTO joinRoom(String roomCode, JoinRoomRequestDTO request);
    EngineGameStateResponseDTO startGame(String roomCode, Long userId);
    EngineGameStateResponseDTO restartGame(String roomCode, Long userId);
    RoomDetailsResponseDTO getRoomDetails(String roomCode);
    BoardLayout getBoardLayout(String roomCode);
}
