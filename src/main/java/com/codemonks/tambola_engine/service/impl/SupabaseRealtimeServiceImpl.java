package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.config.SupabaseProperties;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.dto.realtime.RealtimeTambolaStateDTO;
import com.codemonks.tambola_engine.exception.SupabaseStateException;
import com.codemonks.tambola_engine.service.SupabaseRealtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

// Named bean - Ludo ke SupabaseRealtimeServiceImpl se clash na ho isliye.
@Slf4j
@Service("tambolaSupabaseRealtimeServiceImpl")
@RequiredArgsConstructor
public class SupabaseRealtimeServiceImpl implements SupabaseRealtimeService {

    // Tambola-specific named beans (SupabaseConfig se) - generic naam use
    // karte to Ludo ke bean ke saath ambiguity ho jaati (Spring confuse
    // ho jaata "kaunsa RestClient inject karu").
    private final RestClient tambolaSupabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public void upsertGameState(TambolaGameState state) {
        String table = properties.getTables().getRealtimeGameState();

        // TambolaGameState (domain object) ko realtime-DTO me convert
        // karte hain - poora nested-data ek Map ke andar pack karke.
        RealtimeTambolaStateDTO dto = toRealtimeStateDTO(state);

        try {
            tambolaSupabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    // merge-duplicates: agar is roomId ki row already
                    // hai (pichhle number-call ya claim se), to UPDATE
                    // ho jaayegi, naya row nahi banega - primary key
                    // room_id hi hai (Ludo table ke schema me confirm
                    // kiya tha).
                    .header("Prefer", "resolution=merge-duplicates,return=minimal")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to upsert Tambola game state to Supabase. roomId={}", state.getRoomId(), e);
            throw new SupabaseStateException("Failed to upsert game state for roomId=" + state.getRoomId(), e);
        }
    }

    // Domain TambolaGameState ko realtime-persistable DTO me convert
    // karta hai - saara "game-specific" data (numbers, tickets, claims,
    // rules) ek hi nested Map ke andar pack hota hai (game_state_data
    // jsonb column ke liye).
    private RealtimeTambolaStateDTO toRealtimeStateDTO(TambolaGameState state) {

        Map<String, Object> gameStateData = new HashMap<>();
        gameStateData.put("calledNumbers", state.getCalledNumbers());
        gameStateData.put("lastCalledNumber", state.getLastCalledNumber());
        gameStateData.put("playerTickets", state.getPlayerTickets());
        gameStateData.put("claims", state.getClaims());
        gameStateData.put("activeRules", state.getActiveRules());
        gameStateData.put("timerIntervalSeconds", state.getTimerIntervalSeconds());

        return RealtimeTambolaStateDTO.builder()
                .roomId(state.getRoomId())
                .gameState(gameStateData)
                .gameStatus(state.getStatus().name())
                // roomCode aur players (PlayerDTO list) abhi TambolaGameState
                // ke paas nahi hain (sirf playerIds - Long - hai) - agar
                // realtime_room_lobby table already players display kar
                // raha hai, to yahan duplicate karna zaroori nahi. Filhal
                // null chhod rahe hain - agar frontend ko yahan bhi players
                // chahiye, TambolaGameState me PlayerDTO list add karni hogi.
                .roomCode(null)
                .players(null)
                .winnerUserId(resolveFinalWinner(state))
                .build();
    }

    // Agar FULL_HOUSE (ya jo bhi highest-order rule ho) claim ho chuki hai,
    // uska player-ID nikaal ke "final winner" ke roop me set karta hai.
    // Baaki rules ke winners claims-list ke andar hi rehte hain.
    private Long resolveFinalWinner(TambolaGameState state) {
        return state.getClaims().stream()
                .filter(claim -> claim.getStatus() == com.codemonks.tambola_engine.enums.ClaimStatusEnum.APPROVED)
                .filter(claim -> isHighestOrderRule(state, claim.getRuleType()))
                .map(claim -> claim.getPlayerId())
                .findFirst()
                .orElse(null);
    }

    private boolean isHighestOrderRule(TambolaGameState state, com.codemonks.tambola_engine.enums.RuleTypeEnum ruleType) {
        int maxOrder = state.getActiveRules().stream()
                .mapToInt(rule -> rule.getOrder() != null ? rule.getOrder() : 0)
                .max()
                .orElse(0);

        return state.getActiveRules().stream()
                .anyMatch(rule -> rule.getRuleType() == ruleType &&
                        rule.getOrder() != null && rule.getOrder() == maxOrder);
    }
}