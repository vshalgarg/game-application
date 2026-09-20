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