package com.codemonks.tambola_engine.controller;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.tambola_engine.dto.request.ClaimRequestDTO;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tambola_engine.dto.response.ClaimResponseDTO;
import com.codemonks.tambola_engine.service.ClaimService;
import com.codemonks.tambola_engine.service.GameSetupService;
import com.codemonks.tambola_engine.service.SupabaseRealtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.codemonks.tambola_engine.constant.ApiConstants.BASE_API;
import static com.codemonks.tambola_engine.constant.ApiConstants.LOBBY;
import static com.codemonks.tambola_engine.constant.ApiConstants.START_GAME;
import static com.codemonks.tambola_engine.constant.ApiConstants.SUBMIT_CLAIM;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@Slf4j
public class TambolaEngineController {

    private final GameSetupService gameSetupService;
    private final ClaimService claimService;
    private final SupabaseRealtimeService supabaseRealtimeService;

    @PostMapping(START_GAME)
    public ResponseEntity<GameSetupResult> startGame(
            @Valid @RequestBody EngineStartGameRequestDTO request) {

        log.info(
                "[START_GAME_REQUEST] Room:{} PlayerCount:{}",
                request.getRoomId(),
                request.getPlayers().size()
        );

        GameSetupResult result =
                gameSetupService.initializeGame(request);

        log.info("[START_GAME_RESPONSE] Room:{} Status:{} TotalTickets:{}",
                request.getRoomId(), result.getStatus(), result.getTotalTicketsGenerated());

        return ResponseEntity.ok(result);
    }

    @PostMapping(SUBMIT_CLAIM)
    public ResponseEntity<ClaimResponseDTO> submitClaim(
            @Valid @RequestBody ClaimRequestDTO request) {

        log.info(
                "[CLAIM_REQUEST] Room:{} Player:{} Rule:{}",
                request.getRoomId(),
                request.getPlayerId(),
                request.getRuleType()
        );

        ClaimResponseDTO result =
                claimService.submitClaim(request);

        log.info("[CLAIM_RESPONSE] Room:{} Player:{} Rule:{} ClaimId:{} Status:{}",
                request.getRoomId(), request.getPlayerId(), request.getRuleType(),
                result.getClaimId(), result.getStatus());

        return ResponseEntity.ok(result);
    }

    @PostMapping(LOBBY)
    public ResponseEntity<Void> publishLobby(
            @RequestBody RealtimeLobbyDTO request) {

        log.info(
                "[LOBBY_REQUEST] Room:{} RoomCode:{}",
                request.getRoomId(),
                request.getRoomCode()
        );

        supabaseRealtimeService.publishLobbyState(request);

        return ResponseEntity.ok().build();
    }
}
