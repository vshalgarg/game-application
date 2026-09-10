package com.codemonks.tambola_engine.domain.game;

import com.codemonks.tambola_engine.domain.claim.Claim;
import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
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

    // NAYA: Ek player ne kisi rule ke against valid-pattern claim ki hai -
    // ye method ATOMICALLY check karta hai "kya slot bacha hai?" aur agar
    // haan, to turant us player ko winner-list me daal deta hai.
    //
    // synchronized isliye CRITICAL hai: agar rule ka limit 5 hai aur
    // abhi 4 winners already hain (1 slot bacha), aur EK HI SAMAY pe
    // 3 players claim bhejein - bina synchronized ke, teeno "slot khaali
    // hai" dekh lenge (kyunki koi update abhi hua hi nahi tha), aur
    // teeno approve ho jaayenge - 6 winners ban jaayenge jabki limit
    // 5 thi. synchronized Java ko force karta hai ki ek waqt me sirf
    // EK thread hi is method ke andar ja sake - baaki sab queue me
    // wait karte hain apni baari ka. Isse limit kabhi overshoot nahi hota,
    // aur jo pehle lock le paaya (chahe milliseconds ka hi farak ho)
    // wahi jeetega - baaki fair-tarike se reject honge.
    public synchronized boolean tryClaimSlot(RuleTypeEnum ruleType, Long playerId) {
        GameRule rule = findRule(ruleType);

        if (rule == null || !rule.hasOpenSlots()) {
            return false; // rule nahi mili, ya saare slots already fill ho chuke
        }

        rule.getWinnerPlayerIds().add(playerId);
        return true;
    }

    // Helper - activeRules me se diya gaya ruleType dhoondta hai.
    // Public rakha hai kyunki ClaimServiceImpl ko bhi "configured hai ya
    // nahi" check karne ke liye chahiye, tryClaimSlot() se pehle hi.
    public GameRule findRule(RuleTypeEnum ruleType) {
        return activeRules.stream()
                .filter(r -> r.getRuleType() == ruleType)
                .findFirst()
                .orElse(null);
    }

}