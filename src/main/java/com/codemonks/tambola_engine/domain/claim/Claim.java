package com.codemonks.tambola_engine.domain.claim;

import com.codemonks.tambola_engine.enums.ClaimStatusEnum;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents a single claim submitted by a player during a game
 * (e.g. "I have Full House on ticket 9981").
 * <p>
 * TambolaGameState keeps a running list of these for audit/history,
 * and to check whether a given RuleTypeEnum has already been won.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    private Long claimId;
    private Long playerId;
    private Long ticketId;
    private RuleTypeEnum ruleType;
    private ClaimStatusEnum status;
    private Instant submittedAt;
}