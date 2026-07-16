package com.codemonks.ludo_engine.controller;


import com.codemonks.ludo_engine.constant.ApiConstants;
import com.codemonks.ludo_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.ludo_engine.dto.request.DiceRollRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.ludo_engine.dto.response.DiceRollResponseDTO;
import com.codemonks.ludo_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.ludo_engine.service.Impl.EngineServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.codemonks.ludo_engine.constant.ApiConstants.*;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@Slf4j
public class LudoEngineController {

    private final EngineServiceImpl engineService;

    @PostMapping(START_GAME)
    public ResponseEntity<EngineGameStateResponseDTO>startGame(@Valid @RequestBody EngineStartGameRequestDTO request){

        log.info("Start game request received for room:{}", request.getRoomCode());

        return ResponseEntity.ok(engineService.startGame(request));

    }
    @PostMapping(PROCESS_MOVE)
    public ResponseEntity<EngineGameStateResponseDTO> processmove(
            @Valid @RequestBody EngineMoveRequestDTO request
    ){
        return ResponseEntity.ok(engineService.processMove(request));
    }

    @PostMapping(LOBBY)
    public ResponseEntity<Void> publishLobby(@RequestBody RealtimeLobbyDTO request) {
        log.info("Lobby publish received for roomId:{}", request.getRoomId());
        engineService.publishLobbyState(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.ROLL_DICE)
    public ResponseEntity<DiceRollResponseDTO>rollDice(@RequestBody DiceRollRequestDTO request) {

        log.info("Dice roll requested for RoomId:{} | PlayerId:{}", request.getRoomId(),request.getPlayerId());
        return ResponseEntity.ok(engineService.rollDice(request));
    }
}
