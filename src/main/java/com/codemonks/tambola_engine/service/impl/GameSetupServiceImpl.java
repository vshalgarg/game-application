package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.domain.game.GameStateRegistry;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tambola_engine.dto.request.RuleConfigRequestDTO;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.service.GameSetupService;
import com.codemonks.tambola_engine.service.SupabaseRealtimeService;
import com.codemonks.tambola_engine.service.TimerService;
import com.codemonks.tambola_engine.util.TicketGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameSetupServiceImpl implements GameSetupService {

    private final TicketGenerator ticketGenerator;
    private final GameStateRegistry gameStateRegistry;
    private final TimerService timerService;

    // NAYA: setup poora hote hi initial-state ko turant Supabase me
    // persist karna hai (taaki frontend ko room-live-hone ka pehla
    // update mile - tickets, calledNumbers=empty, status=RUNNING).
    private final SupabaseRealtimeService supabaseRealtimeService;

    private static final int MAX_TICKETS_PER_PLAYER = 4;

    @Override
    public GameSetupResult initializeGame(EngineStartGameRequestDTO request) {

        log.info("[TAMBOLA_SETUP_START] Room:{} Players:{}",
                request.getRoomId(), request.getPlayers().size());

        List<GameRule> activeRules = buildActiveRules(request.getRules());

        TambolaGameState gameState = new TambolaGameState(
                request.getRoomId(),
                request.getTimerIntervalSeconds(),
                activeRules
        );

        // Ab sirf COUNT track karte hain - poori ticket-data collect
        // karne ki zaroorat nahi (pehle RealtimeTicketDTO list banti thi,
        // ab TambolaGameState khud hi Supabase me upsert ho jaayega).
        int totalTicketsGenerated = 0;

        for (PlayerDTO playerDTO : request.getPlayers()) {

            Long playerId = playerDTO.getPlayerId();
            int requestedTicketCount = resolveTicketCount(playerDTO);
            gameState.getPlayerIds().add(playerId);

            List<TambolaTicket> playerTickets = new ArrayList<>();
            for (int i = 0; i < requestedTicketCount; i++) {
                TambolaTicket ticket = ticketGenerator.generateTicket(playerId);
                playerTickets.add(ticket);
            }

            gameState.getPlayerTickets().put(playerId, playerTickets);
            totalTicketsGenerated += playerTickets.size();
        }
        gameStateRegistry.register(gameState);
        gameState.setStatus(GameStatusEnum.RUNNING);
        timerService.startTimer(request.getRoomId());

        // NAYA: setup + tickets + RUNNING-status - sab ek saath Supabase
        // me persist karo, taaki frontend turant live-room dekh sake
        // (Supabase-realtime subscription se).
        supabaseRealtimeService.upsertGameState(gameState);

        log.info("[TAMBOLA_SETUP_COMPLETED] Room:{} TotalTicketsGenerated:{} Status:{}",
                request.getRoomId(), totalTicketsGenerated, gameState.getStatus());

        return new GameSetupResult(
                request.getRoomId(),
                gameState.getStatus().name(),
                totalTicketsGenerated
        );
    }

    private List<GameRule> buildActiveRules(List<RuleConfigRequestDTO> ruleConfigs) {
        List<GameRule> rules = new ArrayList<>();
        for (RuleConfigRequestDTO config : ruleConfigs) {
            GameRule rule = new GameRule();
            rule.setRuleType(config.getRuleType());
            rule.setOrder(config.getOrder());

            // Agar host ne maxWinners nahi bheja, default 1 (single-winner,
            // purane behavior jaisa) - taaki backward-compatible rahe.
            rule.setMaxWinners(
                    config.getMaxWinners() != null && config.getMaxWinners() >= 1
                            ? config.getMaxWinners()
                            : 1
            );

            // Naya rule - koi bhi winner abhi tak nahi, empty list se shuru.
            rule.setWinnerPlayerIds(new ArrayList<>());

            rule.setThreshold(config.getThreshold());
            rules.add(rule);
        }
        return rules;
    }

    private int resolveTicketCount(PlayerDTO playerDTO) {
        Integer requested = playerDTO.getTicketCount();

        if (requested == null || requested < 1) {
            return 1;
        }

        if (requested > MAX_TICKETS_PER_PLAYER) {
            log.warn("Player {} requested {} tickets, clamping to max {}",
                    playerDTO.getPlayerId(), requested, MAX_TICKETS_PER_PLAYER);
            return MAX_TICKETS_PER_PLAYER;
        }

        return requested;
    }

    // toRealtimeTicketDTO() method HATA DIYA - ab zaroorat nahi,
    // SupabaseRealtimeServiceImpl khud TambolaGameState se seedha
    // convert karta hai apne andar.
}