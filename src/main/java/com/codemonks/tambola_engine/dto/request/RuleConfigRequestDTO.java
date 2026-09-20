package com.codemonks.tambola_engine.dto.request;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleConfigRequestDTO {


    private RuleTypeEnum ruleType;
    private Integer order;
    private Integer maxWinners;
    private Integer threshold;
}