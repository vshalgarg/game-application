package com.codemonks.gameservice.engineModule.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Tambola-engine ke ReplaceRulesRequestDTO se field-naam-ba-field match karta hai.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaReplaceRulesRequestDTO {
    private Long roomId;
    private List<TambolaRuleConfigRequestDTO> rules;
}