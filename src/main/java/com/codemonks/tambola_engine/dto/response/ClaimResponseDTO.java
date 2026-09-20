package com.codemonks.tambola_engine.dto.response;

import com.codemonks.tambola_engine.enums.ClaimStatusEnum;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseDTO {

    private Long claimId;
    private RuleTypeEnum ruleType;
    private ClaimStatusEnum status;

    private String gameStatus;
    private Integer winnersCount;   // is rule me ab tak kitne jeet chuke
    private Integer maxWinners;
}