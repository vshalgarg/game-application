package com.codemonks.tambola_engine.domain.rule;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRule {
    private RuleTypeEnum ruleType;
    private Integer order;
    private Boolean claimed;
}