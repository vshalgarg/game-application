package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.constants.ApiUrlConstants;
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
    public RoomResponse createRoom(@RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

    @PostMapping(JOIN_ROOM)
    public RoomResponse joinRoom(
            @PathVariable String roomCode,
            @RequestBody JoinRoomRequest request
    ) {
        return roomService.joinRoom(roomCode, request);
    }

    @PostMapping(START_GAME)
    public ResponseEntity<Void> startGame(
            @PathVariable String roomCode,
            @RequestParam String userId) {

        roomService.startGame(roomCode, userId);
        return ResponseEntity.ok().build();
    }
}
