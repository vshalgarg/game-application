package com.codemonks.gameservice.service;


import com.codemonks.gameservice.dto.request.CreateRoomRequest;
import com.codemonks.gameservice.dto.request.JoinRoomRequest;
import com.codemonks.gameservice.dto.response.RoomResponse;

public interface RoomService {

    RoomResponse createRoom(CreateRoomRequest request);
    RoomResponse joinRoom(String roomCode, JoinRoomRequest request);
    void startGame(String roomCode, Long userId);
}
