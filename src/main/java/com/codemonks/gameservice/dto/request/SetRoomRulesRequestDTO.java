package com.codemonks.gameservice.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetRoomRulesRequestDTO {

    private Long hostUserId;
    private List<RuleSelection> rules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleSelection {
        private String ruleType;
        private Integer order;
        private Integer maxWinners;
        private Integer threshold;
    }
}