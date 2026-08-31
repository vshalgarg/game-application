package com.codemonks.tambola_engine.domain.game;

import com.codemonks.tambola_engine.domain.GameRule;
import com.codemonks.tambola_engine.domain.claim.Claim;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the complete in-memory state of a single Tambola room
 * while the game is live.
 * <p>
 * Uses Lombok only for boilerplate getters/setters on plain data
 * fields. equals()/hashCode()/toString() are intentionally NOT
 * Lombok-generated here (unlike the DTOs) because this object is
 * mutable, stateful, and holds a synchronization lock — auto-generated
 * equals/hashCode on a changing Set/Map is a correctness risk, and
 * an auto toString() would dump the entire room's tickets/claims
 * into every log line.
 */
@Getter
public class TambolaGameState {

    @Setter
    private Long roomId;

    @Setter
    private GameStatusEnum status;

    // No @Setter - only mutated internally via callNumber()
    private Set<Integer> calledNumbers;

    // No @Setter - only mutated internally via callNumber()
    private Integer lastCalledNumber;

    @Setter
    private Integer timerIntervalSeconds;

    // No @Setter - populated via getPlayerIds().add(...), not replaced
    private List<Long> playerIds;

    // No @Setter - populated via getPlayerTickets().put(...), not replaced
    private Map<Long, List<TambolaTicket>> playerTickets;

    // No @Setter - populated via getClaims().add(...), not replaced
    private List<Claim> claims;

    // No @Setter - set once at construction
    private List<GameRule> activeRules;

    /**
     * Guards callNumber() against race conditions — e.g. if the
     * scheduled timer thread and a manual "call next number" admin
     * action were ever triggered at the exact same moment.
     */
    private final Object lock = new Object();

    public TambolaGameState(Long roomId, Integer timerIntervalSeconds, List<GameRule> activeRules) {
        this.roomId = roomId;
        this.timerIntervalSeconds = timerIntervalSeconds;
        this.activeRules = activeRules;
        this.status = GameStatusEnum.INITIALIZED;
        this.calledNumbers = new HashSet<>();
        this.playerIds = new ArrayList<>();
        this.playerTickets = new HashMap<>();
        this.claims = new ArrayList<>();
    }

    /**
     * Attempts to record a newly generated number as "called".
     * The only place that mutates calledNumbers, so call-number
     * logic stays encapsulated here rather than scattered across
     * services. Synchronized so a timer tick and a manual override
     * firing at the same instant can't both record a number.
     *
     * @param number the number NumberGeneratorService picked to call
     * @return the same number if newly recorded, or null if it was
     *         already called (caller should pick another and retry)
     */
    public synchronized Integer callNumber(int number) {
        if (calledNumbers.contains(number)) {
            return null;
        }
        calledNumbers.add(number);
        lastCalledNumber = number;
        return number;
    }
}