package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.game.GameSetupResult;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import com.codemonks.tambola_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.tambola_engine.dto.request.RuleConfigRequestDTO;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
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

        log.info(
                "[TAMBOLA_SETUP_START] Room:{} RoomCode:{} Players:{}",
                request.getRoomId(),
                request.getRoomCode(),
                request.getPlayers().size()
        );

        // ---------------------------------------------------------
        // 1. Build and persist the active Tambola rules
        // ---------------------------------------------------------

        List<GameRule> activeRules =
                buildActiveRules(
                        request.getRoomId(),
                        request.getRules()
                );

        ruleRepository.insertAll(activeRules);

        // ---------------------------------------------------------
        // 2. Generate and persist tickets for every player
        // ---------------------------------------------------------

        List<TambolaTicket> allTickets = new ArrayList<>();

        // Runtime representation of Tambola players.
        // This will be stored inside realtime_game_state.players.
        List<PlayerDTO> players = new ArrayList<>();

        for (PlayerDTO playerDTO : request.getPlayers()) {

            Long playerId = playerDTO.getPlayerId();

            int requestedTicketCount =
                    resolveTicketCount(playerDTO);

            // Generate requested number of tickets
            for (int i = 0; i < requestedTicketCount; i++) {

                TambolaTicket ticket =
                        ticketGenerator.generateTicket(playerId);

                // Ticket belongs to this room
                ticket.setRoomId(request.getRoomId());

                allTickets.add(ticket);
            }

            // Keep the actual resolved ticket count
            // in the runtime player representation.
            playerDTO.setTicketCount(requestedTicketCount);

            // New game => nobody has won yet.
            playerDTO.setHasWon(false);

            players.add(playerDTO);
        }

        ticketRepository.insertAll(allTickets);

        // ---------------------------------------------------------
        // 3. Create and persist the running game state
        // ---------------------------------------------------------

        Instant now = Instant.now();

        TambolaGameState state = TambolaGameState.builder()
                .roomId(request.getRoomId())
                .roomCode(request.getRoomCode())
                .status(GameStatusEnum.RUNNING)
                .calledNumbers(new ArrayList<>())
                .timerIntervalSeconds(request.getTimerIntervalSeconds())
                .nextTickAt(
                        now.plusSeconds(
                                request.getTimerIntervalSeconds()
                        )
                )
                .players(players)
                .version(0L)
                .build();

        roomRepository.insert(state);

        // ---------------------------------------------------------
        // 4. Setup completed
        // ---------------------------------------------------------

        log.info(
                "[TAMBOLA_SETUP_COMPLETED] Room:{} RoomCode:{} TotalTicketsGenerated:{} Status:{}",
                request.getRoomId(),
                request.getRoomCode(),
                allTickets.size(),
                state.getStatus()
        );

        return GameSetupResult.builder()
                .roomId(request.getRoomId())
                .status(state.getStatus().name())
                .totalTicketsGenerated(allTickets.size())
                .build();
    }

    /**
     * Converts the rule configuration received from Game-Service
     * into Tambola domain rules.
     */
    private List<GameRule> buildActiveRules(
            Long roomId,
            List<RuleConfigRequestDTO> ruleConfigs) {

        List<GameRule> rules = new ArrayList<>();

        if (ruleConfigs == null || ruleConfigs.isEmpty()) {
            return rules;
        }

        for (RuleConfigRequestDTO config : ruleConfigs) {

            GameRule rule = new GameRule();

            rule.setRoomId(roomId);

            rule.setRuleType(config.getRuleType());

            rule.setOrder(config.getOrder());

            // Default max winners = 1
            // if host didn't provide a valid value.
            rule.setMaxWinners(
                    config.getMaxWinners() != null
                            && config.getMaxWinners() >= 1
                            ? config.getMaxWinners()
                            : 1
            );

            // No winner exists when the game starts.
            rule.setWinnerPlayerIds(new ArrayList<>());

            rule.setThreshold(config.getThreshold());

            // Initial optimistic-lock version.
            rule.setVersion(0L);

            rules.add(rule);
        }

        return rules;
    }

    /**
     * Resolves the number of tickets requested by a player.
     *
     * Rules:
     * - null / less than 1 -> 1 ticket
     * - greater than 4 -> clamp to 4
     * - otherwise -> requested value
     */
    private int resolveTicketCount(PlayerDTO playerDTO) {

        Integer requested = playerDTO.getTicketCount();

        if (requested == null || requested < 1) {
            return 1;
        }

        if (requested > MAX_TICKETS_PER_PLAYER) {

            log.warn(
                    "[TAMBOLA_TICKET_LIMIT] Player:{} Requested:{} Max:{}",
                    playerDTO.getPlayerId(),
                    requested,
                    MAX_TICKETS_PER_PLAYER
            );

            return MAX_TICKETS_PER_PLAYER;
        }

        return requested;
    }
}