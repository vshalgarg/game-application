package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.constants.ApiUrlConstants;
import com.codemonks.gameservice.dto.ApiResponse;
import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.dto.request.JoinRoomRequestDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.codemonks.gameservice.constants.ApiUrlConstants.Room.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiUrlConstants.Room.BASE)
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @PostMapping(CREATE_ROOM)
    public ResponseEntity<ApiResponse<RoomResponseDTO>> createRoom(
            @RequestBody CreateRoomRequestDTO request) {

        log.info(
                "Create Room request received. userId={}",
                request.getUserId()
        );

        RoomResponseDTO response = roomService.createRoom(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(JOIN_ROOM)
    public ResponseEntity<ApiResponse<RoomResponseDTO>> joinRoom(
            @PathVariable String roomCode,
            @RequestBody JoinRoomRequestDTO request
    ) {
        log.info(
                "Join Room request received. userId={}",
                request.getUserId()
        );
        RoomResponseDTO response = roomService.joinRoom(roomCode, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(START_GAME)
    public ResponseEntity<ApiResponse<Void>> startGame(
            @PathVariable String roomCode,
            @RequestParam Long userId) {

        log.info(
                "Start game request received. roomCode={}, userId={}",
                roomCode,
                userId
        );

        roomService.startGame(roomCode, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
