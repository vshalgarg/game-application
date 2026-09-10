package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.claim.Claim;
import com.codemonks.tambola_engine.domain.game.GameStateRegistry;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.dto.request.ClaimRequestDTO;
import com.codemonks.tambola_engine.dto.response.ClaimResponseDTO;
import com.codemonks.tambola_engine.enums.ClaimStatusEnum;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import com.codemonks.tambola_engine.exception.InvalidClaimException;
import com.codemonks.tambola_engine.service.ClaimService;
import com.codemonks.tambola_engine.service.ClaimValidationService;
import com.codemonks.tambola_engine.service.SupabaseRealtimeService;
import com.codemonks.tambola_engine.service.TimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private static final int WIN_NOTIFY_DELAY_SECONDS = 3;

    private final GameStateRegistry gameStateRegistry;
    private final ClaimValidationService claimValidationService;
    private final TimerService timerService;
    private final SupabaseRealtimeService supabaseRealtimeService;

    @Override
    public ClaimResponseDTO submitClaim(ClaimRequestDTO request) {

        TambolaGameState state = gameStateRegistry.get(request.getRoomId());

        if (state.getStatus() != GameStatusEnum.RUNNING) {
            throw new InvalidClaimException(
                    TambolaErrorCodesEnum.GAME_NOT_RUNNING,
                    "Cannot submit claim - room " + request.getRoomId() +
                            " is not currently RUNNING (status: " + state.getStatus() + ")"
            );
        }

        // Rule configured hai ya nahi - ye check yahan bas fail-fast ke liye
        // hai (friendly error jaldi milе). Asli/authoritative slot-check
        // niche tryClaimSlot() ke andar hoga, jo thread-safe hai.
        GameRule matchedRule = state.findRule(request.getRuleType());
        if (matchedRule == null) {
            throw new InvalidClaimException(
                    TambolaErrorCodesEnum.RULE_NOT_CONFIGURED,
                    "Rule " + request.getRuleType() + " is not configured for this game"
            );
        }

        // Quick early-check (not atomic, sirf fail-fast ke liye) - agar
        // pehle se hi pura clear dikh raha hai ki slots khatam hain, to
        // bekar me pattern-validation ka kaam mat karo. Asli guarantee
        // niche tryClaimSlot() (synchronized) se hi aati hai.
        if (!matchedRule.hasOpenSlots()) {
            throw new InvalidClaimException(
                    TambolaErrorCodesEnum.RULE_SLOTS_FULL
            );
        }

        TambolaTicket ticket = findTicket(state, request.getPlayerId(), request.getTicketId());

        boolean isValid = claimValidationService.validate(matchedRule, ticket, state.getCalledNumbers());

        if (!isValid) {
            Claim claim = new Claim(
                    generateClaimId(state),
                    request.getPlayerId(),
                    request.getTicketId(),
                    request.getRuleType(),
                    ClaimStatusEnum.REJECTED,
                    Instant.now()
            );
            state.getClaims().add(claim);
            supabaseRealtimeService.upsertGameState(state);

            log.info("[CLAIM_REJECTED] Room:{} Player:{} Rule:{} Reason: pattern mismatch",
                    request.getRoomId(), request.getPlayerId(), request.getRuleType());

            throw new InvalidClaimException(
                    TambolaErrorCodesEnum.INVALID_CLAIM_PATTERN,
                    "Ticket does not satisfy pattern for rule " + request.getRuleType()
            );
        }

        // ASLI CONCURRENCY-SAFE STEP: pattern valid hai, ab atomically
        // check-and-claim karo. Agar multiple players ek hi waqt yahan
        // pahunche, ye method (synchronized, TambolaGameState ke andar)
        // unko ek-ek karke process karega - koi bhi do threads ek saath
        // andar nahi jaa sakte. Isse slot-limit kabhi overshoot nahi hota.
        boolean slotWon = state.tryClaimSlot(request.getRuleType(), request.getPlayerId());

        if (!slotWon) {
            // Pattern valid tha, LEKIN beech me (humare do checks ke
            // beech ke chhote se waqt me) koi doosra player already
            // aakhri slot le chuka - is player ko "slots full" milega.
            Claim claim = new Claim(
                    generateClaimId(state),
                    request.getPlayerId(),
                    request.getTicketId(),
                    request.getRuleType(),
                    ClaimStatusEnum.REJECTED,
                    Instant.now()
            );
            state.getClaims().add(claim);
            supabaseRealtimeService.upsertGameState(state);

            log.info("[CLAIM_REJECTED] Room:{} Player:{} Rule:{} Reason: slots filled by others",
                    request.getRoomId(), request.getPlayerId(), request.getRuleType());

            throw new InvalidClaimException(TambolaErrorCodesEnum.RULE_SLOTS_FULL);
        }

        // Slot mil gaya - approved claim record karo.
        Claim claim = new Claim(
                generateClaimId(state),
                request.getPlayerId(),
                request.getTicketId(),
                request.getRuleType(),
                ClaimStatusEnum.APPROVED,
                Instant.now()
        );
        state.getClaims().add(claim);
        state.setStatus(GameStatusEnum.WIN);

        log.info("[CLAIM_APPROVED] Room:{} Player:{} Rule:{} Winners so far:{}/{}",
                request.getRoomId(), request.getPlayerId(), request.getRuleType(),
                matchedRule.getWinnerPlayerIds().size(), matchedRule.getMaxWinners());

        // Confirm ho chuka: har jeet (chahe beech ka winner ho ya aakhri)
        // par WIN-notify pause hoga.
        supabaseRealtimeService.upsertGameState(state);

        // Game khatam tab hoga jab SAARI rules ke SAARE slots fill ho
        // chuke hon - sirf ek player jeetne se pura rule "close" nahi hota
        // agar aur slots baaki hain.
        boolean allRulesFullyClosed = state.getActiveRules().stream()
                .noneMatch(GameRule::hasOpenSlots);

        if (allRulesFullyClosed) {
            state.setStatus(GameStatusEnum.FINISHED);
            timerService.stopTimer(request.getRoomId());

            log.info("[GAME_FINISHED] Room:{} - all rules fully claimed", request.getRoomId());

            supabaseRealtimeService.upsertGameState(state);
            gameStateRegistry.remove(request.getRoomId());
        } else {
            timerService.scheduleResume(request.getRoomId(), WIN_NOTIFY_DELAY_SECONDS);
        }

        return new ClaimResponseDTO(
                claim.getClaimId(),
                claim.getRuleType(),
                claim.getStatus(),
                state.getStatus().name(),
                matchedRule.getWinnerPlayerIds().size(),
                matchedRule.getMaxWinners()
        );
    }

    private TambolaTicket findTicket(TambolaGameState state, Long playerId, Long ticketId) {
        List<TambolaTicket> playerTickets = state.getPlayerTickets().get(playerId);

        if (playerTickets == null) {
            throw new InvalidClaimException(
                    TambolaErrorCodesEnum.PLAYER_HAS_NO_TICKETS,
                    "Player " + playerId + " has no tickets in this room"
            );
        }

        return playerTickets.stream()
                .filter(t -> t.getTicketId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new InvalidClaimException(
                        TambolaErrorCodesEnum.TICKET_NOT_OWNED,
                        "Ticket " + ticketId + " does not belong to player " + playerId
                ));
    }

    private Long generateClaimId(TambolaGameState state) {
        return (long) state.getClaims().size() + 1;
    }
}