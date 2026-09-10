package com.codemonks.tambola_engine.dto.request;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sent when a player submits a claim (e.g. "I have Full House").
 * The engine validates this against TambolaGameState's calledNumbers
 * and the player's ticket before approving or rejecting it.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequestDTO {

    private Long roomId;
    private Long playerId;
    private Long ticketId;
    private RuleTypeEnum ruleType;
}