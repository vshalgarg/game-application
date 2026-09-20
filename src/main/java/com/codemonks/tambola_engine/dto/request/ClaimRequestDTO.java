package com.codemonks.tambola_engine.dto.request;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequestDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;
    @NotNull(message = "Player ID is required")
    private Long playerId;
    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
    @NotNull(message = "Rule type is required")
    private RuleTypeEnum ruleType;
}