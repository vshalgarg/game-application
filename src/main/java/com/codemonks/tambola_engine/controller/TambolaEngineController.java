package com.codemonks.tambola_engine.controller;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.tambola_engine.dto.request.ClaimRequestDTO;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tambola_engine.dto.request.ReplaceRulesRequestDTO;
import com.codemonks.tambola_engine.dto.response.ClaimResponseDTO;
import com.codemonks.tambola_engine.service.ClaimService;
import com.codemonks.tambola_engine.service.GameSetupService;
import com.codemonks.tambola_engine.service.RuleService;
import com.codemonks.tambola_engine.service.SupabaseRealtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.codemonks.tambola_engine.constant.ApiConstants.BASE_API;
import static com.codemonks.tambola_engine.constant.ApiConstants.LOBBY;
import static com.codemonks.tambola_engine.constant.ApiConstants.RULES;
import static com.codemonks.tambola_engine.constant.ApiConstants.START_GAME;
import static com.codemonks.tambola_engine.constant.ApiConstants.SUBMIT_CLAIM;

@RestController
@RequestMapping(BASE_API)
@RequiredArgsConstructor
@Slf4j
public class TambolaEngineController {

    private final GameSetupService gameSetupService;
    private final ClaimService claimService;
    private final RuleService ruleService;
    private final SupabaseRealtimeService supabaseRealtimeService;

    @PostMapping(START_GAME)
    public ResponseEntity<GameSetupResult> startGame(
            @Valid @RequestBody EngineStartGameRequestDTO request) {

        long startTime = System.currentTimeMillis();

        log.info(
                "[TAMBOLA][START_GAME][REQUEST] roomId={} playerCount={}",
                request.getRoomId(),
                request.getPlayers() != null ? request.getPlayers().size() : 0
        );

        try {

            GameSetupResult result = gameSetupService.initializeGame(request);

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "[TAMBOLA][START_GAME][SUCCESS] roomId={} status={} totalTickets={} durationMs={}",
                    request.getRoomId(),
                    result.getStatus(),
                    result.getTotalTicketsGenerated(),
                    duration
            );

            return ResponseEntity.ok(result);

        } catch (Exception ex) {

            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "[TAMBOLA][START_GAME][FAILED] roomId={} durationMs={} error={}",
                    request.getRoomId(),
                    duration,
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
    }

    @PostMapping(SUBMIT_CLAIM)
    public ResponseEntity<ClaimResponseDTO> submitClaim(
            @Valid @RequestBody ClaimRequestDTO request) {

        long startTime = System.currentTimeMillis();

        log.info(
                "[TAMBOLA][CLAIM][REQUEST] roomId={} playerId={} ruleType={}",
                request.getRoomId(),
                request.getPlayerId(),
                request.getRuleType()
        );

        try {

            ClaimResponseDTO result = claimService.submitClaim(request);

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "[TAMBOLA][CLAIM][SUCCESS] roomId={} playerId={} ruleType={} claimId={} status={} durationMs={}",
                    request.getRoomId(),
                    request.getPlayerId(),
                    request.getRuleType(),
                    result.getClaimId(),
                    result.getStatus(),
                    duration
            );

            return ResponseEntity.ok(result);

        } catch (Exception ex) {

            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "[TAMBOLA][CLAIM][FAILED] roomId={} playerId={} ruleType={} durationMs={} error={}",
                    request.getRoomId(),
                    request.getPlayerId(),
                    request.getRuleType(),
                    duration,
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
    }

    @PostMapping(LOBBY)
    public ResponseEntity<Void> publishLobby(
            @Valid @RequestBody RealtimeLobbyDTO request) {

        long startTime = System.currentTimeMillis();

        log.info(
                "[TAMBOLA][LOBBY][REQUEST] roomId={} roomCode={}",
                request.getRoomId(),
                request.getRoomCode()
        );

        try {

            supabaseRealtimeService.publishLobbyState(request);

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "[TAMBOLA][LOBBY][SUCCESS] roomId={} roomCode={} durationMs={}",
                    request.getRoomId(),
                    request.getRoomCode(),
                    duration
            );

            return ResponseEntity.ok().build();

        } catch (Exception ex) {

            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "[TAMBOLA][LOBBY][FAILED] roomId={} roomCode={} durationMs={} error={}",
                    request.getRoomId(),
                    request.getRoomCode(),
                    duration,
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
    }

    @PutMapping(RULES)
    public ResponseEntity<Void> replaceRules(
            @Valid @RequestBody ReplaceRulesRequestDTO request) {

        long startTime = System.currentTimeMillis();

        int ruleCount = request.getRules() != null
                ? request.getRules().size()
                : 0;

        log.info(
                "[TAMBOLA][RULES][REQUEST] roomId={} roomCode={} ruleCount={}",
                request.getRoomId(),
                request.getRoomCode(),
                ruleCount
        );

        try {

            ruleService.replaceRules(
                    request.getRoomId(),
                    request.getRoomCode(),
                    request.getRules()
            );

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "[TAMBOLA][RULES][SUCCESS] roomId={} roomCode={} ruleCount={} durationMs={}",
                    request.getRoomId(),
                    request.getRoomCode(),
                    ruleCount,
                    duration
            );

            return ResponseEntity.ok().build();

        } catch (Exception ex) {

            long duration = System.currentTimeMillis() - startTime;

            log.error(
                    "[TAMBOLA][RULES][FAILED] roomId={} roomCode={} ruleCount={} durationMs={} error={}",
                    request.getRoomId(),
                    request.getRoomCode(),
                    ruleCount,
                    duration,
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
    }
}