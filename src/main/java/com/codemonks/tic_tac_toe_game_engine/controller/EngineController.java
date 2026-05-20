package com.codemonks.tic_tac_toe_game_engine.controller;

import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineMoveRequestDTO; // Missing import added
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineStartGameRequestDto;
import com.codemonks.tic_tac_toe_game_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.tic_tac_toe_game_engine.service.TicTacToeEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.codemonks.tic_tac_toe_game_engine.constant.EngineApiUrlConstants.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@Slf4j
public class EngineController {

    private final TicTacToeEngine ticTacToeEngine;

    @PostMapping(START_GAME)
    public ResponseEntity<EngineGameStateResponseDTO> startGame(@RequestBody EngineStartGameRequestDto request) {
        log.info("Request received to start game for room: {}", request.getRoomCode());
        // fixed: using ticTacToeEngine instead of engine
        return ResponseEntity.ok(ticTacToeEngine.startGame(request));
    }

    @PostMapping(MAKE_MOVE)
    public ResponseEntity<EngineGameStateResponseDTO> makeMove(@RequestBody EngineMoveRequestDTO request) {
        log.info("Request received to process move for player: {} in game: {}", request.getUserId(), request.getRoomId());
        // fixed: using ticTacToeEngine instead of engine
        return ResponseEntity.ok(ticTacToeEngine.makeMove(request));
    }
}