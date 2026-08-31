package com.codemonks.tambola_engine.domain.claim;

import com.codemonks.tambola_engine.enums.ClaimStatusEnum;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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