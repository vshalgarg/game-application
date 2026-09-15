package com.codemonks.gameservice.engineModule.dto.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tambola-engine ke RuleConfigRequestDTO se field-naam-ba-field match karta hai.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaRuleConfigRequestDTO {
    private String ruleType;
    private Integer order;
    private Integer maxWinners;
    private Integer threshold;
}