package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.constants.ApiUrlConstants;
import com.codemonks.gameservice.dto.ApiResponse;
import com.codemonks.gameservice.dto.request.CreateRoomRequest;
import com.codemonks.gameservice.dto.request.JoinRoomRequest;
import com.codemonks.gameservice.dto.response.RoomResponse;
import com.codemonks.gameservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.codemonks.gameservice.constants.ApiUrlConstants.Room.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiUrlConstants.Room.BASE)
public class RoomController {

    private final RoomService roomService;

    @PostMapping(CREATE_ROOM)
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @RequestBody CreateRoomRequest request) {

        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(JOIN_ROOM)
    public ResponseEntity<ApiResponse<RoomResponse>> joinRoom(
            @PathVariable String roomCode,
            @RequestBody JoinRoomRequest request
    ) {
        RoomResponse response = roomService.joinRoom(roomCode, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(START_GAME)
    public ResponseEntity<ApiResponse<Void>> startGame(
            @PathVariable String roomCode,
            @RequestParam Long userId) {

        roomService.startGame(roomCode, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
