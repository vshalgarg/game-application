package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.constants.ApiUrlConstants;
import com.codemonks.gameservice.dto.ApiResponse;
import com.codemonks.gameservice.dto.ResponseMessages;
import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.codemonks.gameservice.constants.ApiUrlConstants.Game.MAKE_MOVE;

@RestController
@RequestMapping(ApiUrlConstants.Game.BASE)
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @PostMapping(MAKE_MOVE)
    public ResponseEntity<ApiResponse<EngineGameStateResponseDTO>> makeMove(
            @PathVariable String roomCode, @RequestBody MakeMoveRequestDTO request) {

        log.info("Move request received. roomCode={}, userId={}",
                roomCode, request.getUserId());

        EngineGameStateResponseDTO response = gameService.makeMove(roomCode, request);
        return ResponseEntity.ok(ApiResponse.success(response, ResponseMessages.MOVE_PROCESSED));
    }
}
