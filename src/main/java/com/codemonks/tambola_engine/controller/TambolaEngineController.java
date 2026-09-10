package com.codemonks.tambola_engine.controller;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.dto.request.ClaimRequestDTO;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tambola_engine.dto.response.ClaimResponseDTO;
import com.codemonks.tambola_engine.service.ClaimService;
import com.codemonks.tambola_engine.service.GameSetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.codemonks.tambola_engine.constant.ApiConstants.*;

// Tambola engine ka single HTTP entry point - jaisa GameVerse architecture
// me pehle discuss hua tha (RoomController/GameController game-service me
// hain, wo humse HTTP se baat karte hain - engine khud sirf apna kaam
// karta hai, koi cross-engine confusion nahi).
@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@Slf4j
public class TambolaEngineController {

    // Poora setup-orchestration isi service ke andar hai - controller
    // sirf HTTP-layer (request/response wrap) sambhalta hai, koi business
    // logic yahan nahi likhi jaati.
    private final GameSetupService gameSetupService;

    // Host ne "Start Game" dabaya -> game-service ye endpoint call karta
    // hai -> poora room-setup (TambolaGameState + tickets + timer-start)
    // is ek call me ho jaata hai (jaisa GameSetupServiceImpl me discuss
    // kiya - setup aur start ek hi action hai yahan, koi beech ka
    // "initialized but not running" wait-state nahi hai).

    // NAYA: ClaimService inject kiya - claim-submit endpoint ke liye chahiye.
    private final ClaimService claimService;


    @PostMapping(START_GAME)
    public ResponseEntity<GameSetupResult> startGame(
            @Valid @RequestBody EngineStartGameRequestDTO request) {

        log.info("[START_GAME_REQUEST] Room:{} PlayerCount:{}",
                request.getRoomId(), request.getPlayers().size());

        GameSetupResult result = gameSetupService.initializeGame(request);

        return ResponseEntity.ok(result);
    }

    // NAYA: Player jab "Claim" button dabata hai (Early Five/Full House/etc.)
    // -> ye endpoint call hota hai -> ClaimServiceImpl poora validation +
    // state-update + Supabase-persist + timer-signal (WIN/FINISHED) sambhalta hai.
    // InvalidClaimException GlobalExceptionHandler ke through automatically
    // proper error-response me convert ho jaayegi (Ludo ke pattern jaisa,
    // agar Tambola me bhi GlobalExceptionHandler bana hua hai).
    @PostMapping(SUBMIT_CLAIM)
    public ResponseEntity<ClaimResponseDTO> submitClaim(
            @Valid @RequestBody ClaimRequestDTO request) {

        log.info("[CLAIM_REQUEST] Room:{} Player:{} Rule:{}",
                request.getRoomId(), request.getPlayerId(), request.getRuleType());

        ClaimResponseDTO result = claimService.submitClaim(request);

        return ResponseEntity.ok(result);
    }

}