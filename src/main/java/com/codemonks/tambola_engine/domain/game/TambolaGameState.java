package com.codemonks.tambola_engine.domain.game;

import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// STATELESS SNAPSHOT — ye ab "realtime_game_state" table ki ek row ka
// Java-representation hai, ek request ke dauraan padha/update-kiya
// jaata hai, phir bhula diya jaata hai. Koi lock, koi synchronized
// method, koi embedded claims/tickets/rules list NAHI — wo sab ab
// apne-apne alag tables/repositories se independently fetch hote hain
// (TambolaRuleRepository, TambolaTicketRepository, TambolaClaimRepository).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TambolaGameState {

    private Long roomId;
    private String roomCode;
    private GameStatusEnum status;
    private List<Integer> calledNumbers;
    private Integer timerIntervalSeconds;
    private Instant nextTickAt;
    private List<PlayerDTO> players;
    // Concurrency-guard — realtime_game_state.version ke against check hota hai.
    private Long version;
}