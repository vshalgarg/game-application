package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.dto.response.GameStateResponse;
import com.codemonks.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/{roomId}/move")
    public GameStateResponse makeMove(
            @PathVariable Long roomId,
            @RequestBody MakeMoveRequestDTO request
    ) {
        return gameService.makeMove(roomId, request);
    }
}
