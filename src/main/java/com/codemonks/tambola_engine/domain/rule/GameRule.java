package com.codemonks.tambola_engine.domain.rule;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents one claim-type that has been configured as valid for
 * a specific game room, chosen by the host during room setup.
 * <p>
 * A game typically has multiple GameRules active at once (e.g. Early
 * Five, Top Line, Full House). As each one gets successfully claimed,
 * TambolaGameState moves through its WIN status; once every GameRule
 * for the room has been claimed, the game moves to FINISHED.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRule {

    /** Which claim pattern this rule represents. */
    private RuleTypeEnum ruleType;

    /**
     * Order in which this rule should logically be completed
     * (lower = earlier). Used to determine whether FULL_HOUSE
     * (typically the highest order) being claimed means the
     * game is now FINISHED.
     */
    private Integer order;

    //Whether this rule has already been successfully claimed.
    private Boolean claimed;

    // Kitne players is rule ko simultaneously jeet sakte hain.
// Host agar na bheje to service-layer default 1 laga deta hai
// (backward-compatible single-winner behavior).
    private Integer maxWinners;

    // Ab tak jitne players jeet chuke, unki IDs - ORDER MAYNE RAKHTA HAI
    // (pehla winner list ke index-0 pe, doosra index-1 pe, waisa hi).
    // TambolaGameState.tryClaimSlot() hi isme add karta hai (synchronized),
    // koi aur jagah se seedha modify nahi karna.
    private List<Long> winnerPlayerIds;

    // FUTURE-FLEXIBILITY FIELD: abhi ke liye null/unused rahega for most
    // rules. Lekin agar kal koi rule (jaise EARLY_FIVE) ko configurable
    // banana ho ("host chaahe to 5 ki jagah 7 numbers maange"), to
    // ClaimValidationServiceImpl is field ko read karega (hardcoded
    // constant ki jagah) - koi structural change nahi karni padegi,
    // sirf validation-method ke andar ek line badlegi.
    private Integer threshold;

    // Convenience-check: kya is rule me abhi bhi koi slot khaali hai?
    // NOTE: Ye method khud thread-safe nahi hai - isko sirf TambolaGameState
    // ke synchronized method (tryClaimSlot) ke andar se hi authoritative
    // tareeke se use karna hai. Bahar se sirf "quick look" ke liye theek hai
    // (jaise fail-fast UI-hint), final decision hamesha tryClaimSlot() ka hai.
    public boolean hasOpenSlots() {
        return winnerPlayerIds.size() < maxWinners;
    }

}