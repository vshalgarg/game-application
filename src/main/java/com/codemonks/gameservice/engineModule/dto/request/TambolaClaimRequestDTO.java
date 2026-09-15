package com.codemonks.gameservice.engineModule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaClaimRequestDTO {
    private Long roomId;
    private Long playerId;
    private Long ticketId;
    private String ruleType;
}