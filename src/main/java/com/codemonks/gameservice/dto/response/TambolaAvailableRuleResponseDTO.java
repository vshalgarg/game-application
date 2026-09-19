package com.codemonks.gameservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TambolaAvailableRuleResponseDTO {

    private String ruleType;
    private String displayName;
    private String description;
    private Integer threshold;
}