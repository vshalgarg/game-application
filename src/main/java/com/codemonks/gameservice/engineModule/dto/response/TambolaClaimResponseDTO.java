package com.codemonks.gameservice.engineModule.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaClaimResponseDTO {
    private Long claimId;
    private String ruleType;
    private String status;
    private String gameStatus;
    private Integer winnersCount;
    private Integer maxWinners;
}