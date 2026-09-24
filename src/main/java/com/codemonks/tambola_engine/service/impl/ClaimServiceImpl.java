package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.claim.Claim;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.dto.request.ClaimRequestDTO;
import com.codemonks.tambola_engine.dto.response.ClaimResponseDTO;
import com.codemonks.tambola_engine.enums.ClaimStatusEnum;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
import com.codemonks.tambola_engine.exception.InvalidClaimException;
import com.codemonks.tambola_engine.repository.TambolaClaimRepository;
import com.codemonks.tambola_engine.repository.TambolaGameStateRepository;
import com.codemonks.tambola_engine.repository.TambolaRuleRepository;
import com.codemonks.tambola_engine.repository.TambolaTicketRepository;
import com.codemonks.tambola_engine.service.ClaimService;
import com.codemonks.tambola_engine.service.ClaimValidationService;
import com.codemonks.tambola_engine.util.OptimisticRetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final TambolaGameStateRepository roomRepository;
    private final TambolaRuleRepository ruleRepository;
    private final TambolaTicketRepository ticketRepository;
    private final TambolaClaimRepository claimRepository;
    private final ClaimValidationService claimValidationService;

    @Override
    public ClaimResponseDTO submitClaim(ClaimRequestDTO request) {

        // 1) Room padho (fresh, Supabase se — koi in-memory-registry nahi)
        TambolaGameState state = roomRepository.findById(request.getRoomId());
        if (state.getStatus() != GameStatusEnum.RUNNING) {
            throw new InvalidClaimException(TambolaErrorCodesEnum.GAME_NOT_RUNNING,
                    "Room " + request.getRoomId() + " is not RUNNING (status: " + state.getStatus() + ")");
        }

        // 2) Rule padho — fail-fast slot-check
        GameRule rule = ruleRepository.findByRoomAndType(request.getRoomId(), request.getRuleType());
        if (rule == null) {
            throw new InvalidClaimException(TambolaErrorCodesEnum.RULE_NOT_CONFIGURED,
                    "Rule " + request.getRuleType() + " is not configured for this game");
        }
        if (!rule.hasOpenSlots()) {
            throw new InvalidClaimException(TambolaErrorCodesEnum.RULE_SLOTS_FULL);
        }

        // 2.1) Sequential rule progression — sirf current active rule par claim hota hai.
        //      Current = sabse chhota rule_order jis me abhi winner-slots khula ho.
        //      Pehle ka rule complete (slots full) hone tak aage wale rules locked rehte hain.
        List<GameRule> roomRules = ruleRepository.findAllByRoom(request.getRoomId());
        GameRule currentActiveRule = findCurrentActiveRule(roomRules);
        if (!currentActiveRule.getRuleType().equals(request.getRuleType())) {
            throw new InvalidClaimException(TambolaErrorCodesEnum.RULE_NOT_ACTIVE,
                    "Rule " + request.getRuleType() + " is not active yet — complete "
                            + currentActiveRule.getRuleType() + " first.");
        }

        // 3) Ticket padho — pattern validate karo
        TambolaTicket ticket = findTicket(request.getRoomId(), request.getPlayerId(), request.getTicketId());
        Set<Integer> calledNumbersSet = new HashSet<>(state.getCalledNumbers());
        boolean isValid = claimValidationService.validate(rule, ticket, calledNumbersSet);

        if (!isValid) {
            // >>> CHANGED: state.getRoomCode() pass kiya — Claim table me
            // >>> ab room_code NOT NULL hai.
            recordClaim(request, state.getRoomCode(), ClaimStatusEnum.REJECTED);
            log.info("[CLAIM_REJECTED] Room:{} Player:{} Rule:{} Reason: pattern mismatch",
                    request.getRoomId(), request.getPlayerId(), request.getRuleType());
            throw new InvalidClaimException(TambolaErrorCodesEnum.INVALID_CLAIM_PATTERN,
                    "Ticket does not satisfy pattern for rule " + request.getRuleType());
        }

        // 4) ASLI CONCURRENCY-SAFE STEP: version-checked retry-loop se slot reserve karo.
        //    Multi-instance-safe — koi bhi instance ye kar sakta hai, kyunki
        //    lock ab Java-memory me nahi, Supabase-row ke "version" column me hai.
        GameRule[] wonRuleHolder = new GameRule[1];
        boolean slotWon = OptimisticRetry.attempt(() -> {
            GameRule freshRule = ruleRepository.findByRoomAndType(request.getRoomId(), request.getRuleType());
            if (freshRule == null || !freshRule.hasOpenSlots()) return false;

            List<Long> newWinners = new ArrayList<>(freshRule.getWinnerPlayerIds());
            newWinners.add(request.getPlayerId());

            boolean updated = ruleRepository.updateWinnersIfVersionMatches(
                    request.getRoomId(), request.getRuleType(), newWinners, freshRule.getVersion());

            if (updated) {
                freshRule.setWinnerPlayerIds(newWinners);
                wonRuleHolder[0] = freshRule;
            }
            return updated;
        });

        if (!slotWon) {
            recordClaim(request, state.getRoomCode(), ClaimStatusEnum.REJECTED);
            log.info("[CLAIM_REJECTED] Room:{} Player:{} Rule:{} Reason: slots filled by others",
                    request.getRoomId(), request.getPlayerId(), request.getRuleType());
            throw new InvalidClaimException(TambolaErrorCodesEnum.RULE_SLOTS_FULL);
        }

        GameRule wonRule = wonRuleHolder[0];

        // 5) Claim record karo (audit — tambola_claims table)
        Claim claim = recordClaim(request, state.getRoomCode(), ClaimStatusEnum.APPROVED);

        log.info("[CLAIM_APPROVED] Room:{} Player:{} Rule:{} Winners so far:{}/{}",
                request.getRoomId(), request.getPlayerId(), request.getRuleType(),
                wonRule.getWinnerPlayerIds().size(), wonRule.getMaxWinners());

        // 6) Saari active-rules dobara padho — decide karo FINISHED hua ya sirf WIN
        List<GameRule> allRules = ruleRepository.findAllByRoom(request.getRoomId());
        boolean allRulesFullyClosed = allRules.stream().allMatch(r ->
                r.getRuleType().equals(wonRule.getRuleType())
                        ? wonRule.getWinnerPlayerIds().size() >= wonRule.getMaxWinners()
                        : !r.hasOpenSlots()
        );

        GameStatusEnum newStatus = allRulesFullyClosed ? GameStatusEnum.FINISHED : GameStatusEnum.WIN;

        // 7) Room-status update karo — version-checked
        // >>> CHANGED: Pehle sirf "game_status" update hota tha, "next_tick_at"
        // >>> touch hi nahi hota tha — matlab WIN-pause ka actual-duration sirf
        // >>> "jo-bhi-purana-next_tick_at-bacha-tha" hota tha, ek proper
        // >>> announcement-window nahi milta tha.
        // >>>
        // >>> AB: jab status WIN set ho raha ho, next_tick_at ko
        // >>> "purana_next_tick_at + ek_aur_fresh_interval" pe extend kar rahe
        // >>> hain. Isse total-wait = (jo-time-bacha-tha) + (poora-fresh-interval)
        // >>> ho jata hai — jaisa humne decide kiya tha.
        // >>>
        // >>> NOTE: Ye extend-karna sirf WIN ke liye hai, FINISHED ke liye nahi
        // >>> (FINISHED me next_tick_at ka matlab hi nahi, game khatam ho chuka).
        // >>>
        // >>> IMPORTANT: Iske baad Supabase pg_cron (process_due_tambola_rooms)
        // >>> jab is WIN-room ko RUNNING me resume karega, wo next_tick_at ko
        // >>> AGE NAHI BADHAYEGA (Option-A decision) — kyunki yahi extend-karna
        // >>> hi poora buffer de chuka hai. Isliye ye dono-changes (yahan + SQL)
        // >>> ek-saath hi deploy/consistent hone chahiye.
        OptimisticRetry.attempt(() -> {
            TambolaGameState freshState = roomRepository.findById(request.getRoomId());

            Map<String, Object> changes = new HashMap<>();
            changes.put("game_status", newStatus.name());

            if (newStatus == GameStatusEnum.WIN
                    && freshState.getNextTickAt() != null
                    && freshState.getTimerIntervalSeconds() != null) {
                Instant extendedTick = freshState.getNextTickAt()
                        .plusSeconds(freshState.getTimerIntervalSeconds());
                changes.put("next_tick_at", extendedTick.toString());
            }

            return roomRepository.updateIfVersionMatches(
                    request.getRoomId(),
                    changes,
                    freshState.getVersion());
        });

        if (allRulesFullyClosed) {
            log.info("[GAME_FINISHED] Room:{} - all rules fully claimed", request.getRoomId());
        }

        return new ClaimResponseDTO(
                claim.getClaimId(),
                claim.getRuleType(),
                claim.getStatus(),
                newStatus.name(),
                wonRule.getWinnerPlayerIds().size(),
                wonRule.getMaxWinners()
        );
    }

    // >>> CHANGED: roomCode-parameter add kiya, aur Claim-constructor-call
    // >>> me bhi naya-field-order match karaya (Claim.java me roomId ke
    // >>> turant-baad roomCode field hai ab).
    private Claim recordClaim(ClaimRequestDTO request, String roomCode, ClaimStatusEnum status) {
        Claim claim = new Claim(
                generateClaimId(),
                request.getRoomId(),
                roomCode,
                request.getPlayerId(),
                request.getTicketId(),
                request.getRuleType(),
                status,
                Instant.now()
        );
        claimRepository.insert(claim);
        return claim;
    }

    private GameRule findCurrentActiveRule(List<GameRule> allRules) {
        return allRules.stream()
                .filter(GameRule::hasOpenSlots)
                .min(Comparator.comparing(GameRule::getOrder))
                .orElseThrow(() -> new InvalidClaimException(
                        TambolaErrorCodesEnum.RULE_SLOTS_FULL,
                        "All rules for this game are already complete"));
    }

    private TambolaTicket findTicket(Long roomId, Long playerId, Long ticketId) {
        return ticketRepository.findByRoomAndPlayer(roomId, playerId).stream()
                .filter(t -> t.getTicketId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new InvalidClaimException(
                        TambolaErrorCodesEnum.TICKET_NOT_OWNED,
                        "Ticket " + ticketId + " does not belong to player " + playerId));
    }

    private Long generateClaimId() {
        // NOTE: agar tambola_claims.claim_id DB-side auto-generate (IDENTITY)
        // hota hai, to ye method delete karo, insert-response se ID lo.
        return Instant.now().toEpochMilli();
    }
}