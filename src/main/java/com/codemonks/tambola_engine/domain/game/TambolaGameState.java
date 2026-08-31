package com.codemonks.tambola_engine.domain.game;

import com.codemonks.tambola_engine.domain.claim.Claim;
import com.codemonks.tambola_engine.domain.rule.GameRule;
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


@Getter
public class TambolaGameState {

    @Setter
    private Long roomId;

    @Setter
    private GameStatusEnum status;
    private Set<Integer> calledNumbers;
    private Integer lastCalledNumber;

    @Setter
    private Integer timerIntervalSeconds;
    private List<Long> playerIds;
    private Map<Long, List<TambolaTicket>> playerTickets;
    private List<Claim> claims;
    private List<GameRule> activeRules;
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
    public synchronized Integer callNumber(int number) {
        if (calledNumbers.contains(number)) {
            return null;
        }
        calledNumbers.add(number);
        lastCalledNumber = number;
        return number;
    }
}