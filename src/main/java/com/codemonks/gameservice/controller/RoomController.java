package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.constants.ApiUrlConstants;
import com.codemonks.gameservice.dto.ApiResponse;
import com.codemonks.gameservice.dto.ResponseMessages;
import com.codemonks.gameservice.dto.request.*;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.DiceRollResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.service.GameService;
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
    private final GameService gameService;

    @PostMapping(CREATE_ROOM)
    public ResponseEntity<ApiResponse<RoomResponseDTO>> createRoom(
            @RequestBody CreateRoomRequestDTO request) {

        log.info(
                "Create Room request received. userId={}",
                request.getUserId()
        );

        RoomResponseDTO response = roomService.createRoom(request);
        return ResponseEntity.ok(ApiResponse.success(response,
                ResponseMessages.ROOM_CREATED));
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
        return ResponseEntity.ok(ApiResponse.success(response,ResponseMessages.ROOM_JOINED));
    }

    @PostMapping(START_GAME)
    public ResponseEntity<ApiResponse<EngineGameStateResponseDTO>> startGame(
            @PathVariable String roomCode,
            @RequestParam Long userId) {

        log.info(
                "Start game request received. roomCode={}, userId={}",
                roomCode,
                userId
        );

        EngineGameStateResponseDTO response = roomService.startGame(roomCode, userId);
        return ResponseEntity.ok(ApiResponse.success(response,ResponseMessages.GAME_STARTED));
    }

    @PostMapping(RESTART_GAME)
    public ResponseEntity<ApiResponse<EngineGameStateResponseDTO>> restartGame(
            @PathVariable String roomCode,
            @RequestParam Long userId) {

        log.info(
                "Restart game request received. roomCode={}, userId={}",
                roomCode,
                userId
        );

        EngineGameStateResponseDTO response = roomService.restartGame(roomCode, userId);
        return ResponseEntity.ok(ApiResponse.success(response,ResponseMessages.GAME_RESTARTED));
    }

    @GetMapping(GET_ROOM_DETAILS)
    public ResponseEntity<ApiResponse<RoomDetailsResponseDTO>> getRoomDetails(
            @PathVariable String roomCode
    ) {
        RoomDetailsResponseDTO response =
                roomService.getRoomDetails(roomCode);
        return ResponseEntity.ok(
                ApiResponse.success(response,ResponseMessages.ROOM_DETAILS_FETCHED)
        );
    }

    @PostMapping(ROLL_DICE)
    public ResponseEntity<ApiResponse<DiceRollResponseDTO>> rollDice(
            @PathVariable String roomCode,
            @RequestBody RollDiceRequestDTO request
    ) {
        log.info("Roll dice request. roomCode={}, userId={}", roomCode, request.getUserId());
        DiceRollResponseDTO response = gameService.rollDice(roomCode, request.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(ADD_BOT)
    public ResponseEntity<ApiResponse<RoomDetailsResponseDTO>> addBot(
            @PathVariable String roomCode,
            @RequestBody AddBotRequestDTO request
    ) {

        log.info(
                "Add bot request received. roomCode={}, hostUserId={}",
                roomCode,
                request.getHostUserId()
        );

        RoomActionResponseDTO response =
                roomService.addBot(roomCode, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response.getRoomDetails(),
                        response.getMessage()
                )
        );
    }
    @DeleteMapping(REMOVE_PLAYER)
    public ResponseEntity<ApiResponse<RoomDetailsResponseDTO>> removePlayer(
            @PathVariable String roomCode,
            @RequestBody RemovePlayerRequestDTO request
    ) {

        log.info(
                "Remove participant request received. roomCode={}, hostUserId={}, userId={}",
                roomCode,
                request.getHostUserId(),
                request.getUserId()
        );

        RoomActionResponseDTO response =
                roomService.removePlayer(roomCode, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response.getRoomDetails(),
                        response.getMessage()
                )
        );
    }
}
