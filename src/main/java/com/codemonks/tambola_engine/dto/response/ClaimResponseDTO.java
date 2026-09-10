package com.codemonks.tambola_engine.dto.response;

import com.codemonks.tambola_engine.enums.ClaimStatusEnum;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Claim submit karne ke baad caller (controller) ko ye response milta
// hai - approved hua ya reject, konsi rule thi, kaunsi game-status ab hai.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseDTO {

    private Long claimId;
    private RuleTypeEnum ruleType;
    private ClaimStatusEnum status;

    // Game ab kis status pe hai claim-process hone ke baad
    // (WIN agar approved hua, RUNNING agar reject hua aur game chal
    // raha hai, FINISHED agar ye last rule thi).
    private String gameStatus;
    private Integer winnersCount;   // is rule me ab tak kitne jeet chuke
    private Integer maxWinners;
}