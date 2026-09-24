package com.codemonks.tambola_engine.domain.claim;

import com.codemonks.tambola_engine.enums.ClaimStatusEnum;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @JsonProperty("claim_id")
    private Long claimId;

    @JsonProperty("room_id")
    private Long roomId;

    // >>> CHANGED: naya field add kiya — tambola_claims table me ab
    // >>> room_code column hai (NOT NULL). NOTE: @AllArgsConstructor
    // >>> use karta hai, isliye is field ki position IMPORTANT hai —
    // >>> jahan bhi "new Claim(...)" call hota hai (ClaimServiceImpl
    // >>> me), wahan constructor-args ka order isी se match karna
    // >>> padega. Maine roomId ke turant baad rakha hai for consistency.
    @JsonProperty("room_code")
    private String roomCode;

    @JsonProperty("player_id")
    private Long playerId;

    @JsonProperty("ticket_id")
    private Long ticketId;

    @JsonProperty("rule_type")
    private RuleTypeEnum ruleType;

    @JsonProperty("status")
    private ClaimStatusEnum status;

    @JsonProperty("submitted_at")
    private Instant submittedAt;
}