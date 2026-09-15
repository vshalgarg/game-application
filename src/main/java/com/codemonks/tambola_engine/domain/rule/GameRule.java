package com.codemonks.tambola_engine.domain.rule;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents one claim-type that has been configured as valid for
 * a specific game room, chosen by the host during room setup.
 *
 * A game typically has multiple GameRules active at once
 * (e.g. Early Five, Top Line, Full House).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRule {

    @JsonProperty("room_id")
    private Long roomId;

    /** Which claim pattern this rule represents. */
    @JsonProperty("rule_type")
    private RuleTypeEnum ruleType;

    /**
     * Order in which this rule should logically be completed
     * (lower = earlier).
     */
    @JsonProperty("rule_order")
    private Integer order;

    /**
     * Whether this rule has already been successfully claimed.
     *
     * This is a domain/runtime field.
     * It is NOT stored in realtime_tambola_rules.
     */
    @JsonIgnore
    private Boolean claimed;

    /**
     * Maximum number of players that can simultaneously
     * win this rule.
     *
     * Host agar na bheje to service-layer default 1 laga deta hai.
     */
    @JsonProperty("max_winners")
    private Integer maxWinners;

    /**
     * Players who have already successfully claimed this rule.
     *
     * Order matters:
     * first winner -> index 0
     * second winner -> index 1
     * etc.
     */
    @JsonProperty("winner_player_ids")
    private List<Long> winnerPlayerIds;

    /**
     * Optional configurable threshold for a rule.
     *
     * Example:
     * EARLY_FIVE -> 5
     * Future configurable rule -> another value
     */
    private Integer threshold;

    /**
     * realtime_tambola_rules.version ke against optimistic
     * concurrency check ke liye use hota hai.
     */
    private Long version;

    /**
     * Convenience-check:
     * kya is rule me abhi bhi koi winner slot khaali hai?
     *
     * NOTE:
     * Ye method khud thread-safe nahi hai.
     * Authoritative decision TambolaGameState.tryClaimSlot()
     * ke synchronized flow ke andar hi hona chahiye.
     */
    public boolean hasOpenSlots() {
        return winnerPlayerIds.size() < maxWinners;
    }
}