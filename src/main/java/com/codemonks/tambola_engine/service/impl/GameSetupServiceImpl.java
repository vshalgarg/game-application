package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tambola_engine.dto.request.RuleConfigRequestDTO;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import com.codemonks.tambola_engine.exception.RoomNotFoundException;
import com.codemonks.tambola_engine.repository.TambolaGameStateRepository;
import com.codemonks.tambola_engine.repository.TambolaRuleRepository;
import com.codemonks.tambola_engine.repository.TambolaTicketRepository;
import com.codemonks.tambola_engine.service.GameSetupService;
import com.codemonks.tambola_engine.util.TicketGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("tambolaGameSetupService")
@RequiredArgsConstructor
public class GameSetupServiceImpl implements GameSetupService {

    private static final int MAX_TICKETS_PER_PLAYER = 4;

    private final TicketGenerator ticketGenerator;
    private final TambolaGameStateRepository roomRepository;
    private final TambolaRuleRepository ruleRepository;
    private final TambolaTicketRepository ticketRepository;

    @Override
    public GameSetupResult initializeGame(EngineStartGameRequestDTO request) {

        log.info("[TAMBOLA_SETUP_START] Room:{} RoomCode:{} TimerInterval:{}s Players:{}",
                request.getRoomId(), request.getRoomCode(),
                request.getTimerIntervalSeconds(),
                request.getPlayers().stream().map(PlayerDTO::getPlayerId).toList());

        // ---------------------------------------------------------
        // 1. Rules — per-resource idempotency check.
        //    Agar is room ke liye rules already Supabase me hain
        //    (pichli successful/partial attempt se), dobara insert
        //    mat karo - warna (room_id, rule_type) primary-key
        //    conflict aayega (jo humne production-logs me dekha).
        // ---------------------------------------------------------
        List<GameRule> existingRules = ruleRepository.findAllByRoom(request.getRoomId());

        if (!existingRules.isEmpty()) {
            log.info("[TAMBOLA_SETUP_SKIP_RULES] Room:{} already has {} rule(s) configured - skipping insert",
                    request.getRoomId(), existingRules.size());
        } else {
            List<GameRule> activeRules = buildActiveRules(request.getRoomId(), request.getRules());
            ruleRepository.insertAll(activeRules);
            log.info("[TAMBOLA_SETUP_RULES_INSERTED] Room:{} RuleCount:{}",
                    request.getRoomId(), activeRules.size());
        }

        // ---------------------------------------------------------
        // 2. Tickets — per-player idempotency check.
        //    tambola_tickets ke paas koi natural unique-constraint
        //    nahi hai (ticket_id sirf auto-increment hai) - isliye
        //    ye check khud humein karna padta hai, warna retry pe
        //    silently duplicate tickets ban jaate hain (koi error
        //    nahi aata, jo sabse khatarnak scenario hai).
        // ---------------------------------------------------------
        List<PlayerDTO> players = new ArrayList<>();
        int totalTickets = 0;

        for (PlayerDTO playerDTO : request.getPlayers()) {

            Long playerId = playerDTO.getPlayerId();

            List<TambolaTicket> existingTickets =
                    ticketRepository.findByRoomAndPlayer(request.getRoomId(), playerId);

            int ticketCountForPlayer;

            if (!existingTickets.isEmpty()) {
                log.info("[TAMBOLA_SETUP_SKIP_TICKETS] Room:{} Player:{} already has {} ticket(s) - skipping generation",
                        request.getRoomId(), playerId, existingTickets.size());
                ticketCountForPlayer = existingTickets.size();
            } else {
                int requestedTicketCount = resolveTicketCount(playerDTO);
                List<TambolaTicket> newTickets = new ArrayList<>();
                for (int i = 0; i < requestedTicketCount; i++) {
                    TambolaTicket ticket = ticketGenerator.generateTicket(playerId);
                    ticket.setRoomId(request.getRoomId());
                    newTickets.add(ticket);
                }
                ticketRepository.insertAll(newTickets);
                log.info("[TAMBOLA_SETUP_TICKETS_INSERTED] Room:{} Player:{} TicketCount:{}",
                        request.getRoomId(), playerId, newTickets.size());
                ticketCountForPlayer = newTickets.size();
            }

            totalTickets += ticketCountForPlayer;

            playerDTO.setTicketCount(ticketCountForPlayer);
            playerDTO.setHasWon(false);
            players.add(playerDTO);
        }

        // ---------------------------------------------------------
        // 3. Room-state row — idempotency check.
        //    findById() RoomNotFoundException throw karta hai jab
        //    row na mile (doosri repos ki tarah null/empty-list nahi
        //    deta) - isliye try-catch se check karna padta hai.
        // ---------------------------------------------------------
        TambolaGameState existingState = null;
        try {
            existingState = roomRepository.findById(request.getRoomId());
        } catch (RoomNotFoundException e) {
            // Expected on first-ever call for this room - room abhi tak
            // initialize nahi hui, aage insert karenge.
            log.debug("[TAMBOLA_SETUP_ROOM_ABSENT] Room:{} not found - will be created", request.getRoomId());
        }

        GameStatusEnum finalStatus;

        if (existingState != null) {
            log.info("[TAMBOLA_SETUP_SKIP_ROOM] Room:{} already initialized (status={}) - skipping room-row insert",
                    request.getRoomId(), existingState.getStatus());
            finalStatus = existingState.getStatus();
        } else {
            Instant now = Instant.now();
            TambolaGameState state = TambolaGameState.builder()
                    .roomId(request.getRoomId())
                    .roomCode(request.getRoomCode())
                    .status(GameStatusEnum.RUNNING)
                    .calledNumbers(new ArrayList<>())
                    .timerIntervalSeconds(request.getTimerIntervalSeconds())
                    .nextTickAt(now.plusSeconds(request.getTimerIntervalSeconds()))
                    .players(players)
                    .version(0L)
                    .build();

            roomRepository.insert(state);
            log.info("[TAMBOLA_SETUP_ROOM_INSERTED] Room:{} RoomCode:{} Status:{}",
                    request.getRoomId(), request.getRoomCode(), state.getStatus());
            finalStatus = state.getStatus();
        }

        log.info("[TAMBOLA_SETUP_COMPLETED] Room:{} RoomCode:{} TotalTickets:{} Status:{}",
                request.getRoomId(), request.getRoomCode(), totalTickets, finalStatus);

        return GameSetupResult.builder()
                .roomId(request.getRoomId())
                .status(finalStatus.name())
                .totalTicketsGenerated(totalTickets)
                .build();
    }

    private List<GameRule> buildActiveRules(Long roomId, List<RuleConfigRequestDTO> ruleConfigs) {
        List<GameRule> rules = new ArrayList<>();

        if (ruleConfigs == null || ruleConfigs.isEmpty()) {
            return rules;
        }

        for (RuleConfigRequestDTO config : ruleConfigs) {
            GameRule rule = new GameRule();
            rule.setRoomId(roomId);
            rule.setRuleType(config.getRuleType());
            rule.setOrder(config.getOrder());
            rule.setMaxWinners(
                    config.getMaxWinners() != null && config.getMaxWinners() >= 1
                            ? config.getMaxWinners()
                            : 1
            );
            rule.setWinnerPlayerIds(new ArrayList<>());
            rule.setThreshold(resolveThreshold(config.getRuleType(), config.getThreshold()));
            rule.setVersion(0L);
            rules.add(rule);
        }

        return rules;
    }

    /**
     * Threshold is a static/master rule property. Engine-side default ke
     * against resolve hota hai — client/auth kabhi bhi arbitrary threshold
     * nahi bhejega. EARLY_FIVE keliye 5 default.
     */
    private Integer resolveThreshold(RuleTypeEnum ruleType, Integer requested) {
        if (ruleType == RuleTypeEnum.EARLY_FIVE && (requested == null || requested < 1)) {
            return 5;
        }
        return requested;
    }

    private int resolveTicketCount(PlayerDTO playerDTO) {
        Integer requested = playerDTO.getTicketCount();

        if (requested == null || requested < 1) {
            return 1;
        }

        if (requested > MAX_TICKETS_PER_PLAYER) {
            log.warn("[TAMBOLA_TICKET_LIMIT] Player:{} Requested:{} Max:{}",
                    playerDTO.getPlayerId(), requested, MAX_TICKETS_PER_PLAYER);
            return MAX_TICKETS_PER_PLAYER;
        }

        return requested;
    }
}