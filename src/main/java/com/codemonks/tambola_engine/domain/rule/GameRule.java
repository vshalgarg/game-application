package com.codemonks.tambola_engine.domain;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /** Whether this rule has already been successfully claimed. */
    private Boolean claimed;
}