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

    // Host ne "Waiting Room" me jo rules tick kiye (1st line, 2nd line,
    // Full house, Corners waghera) - saath me maxWinners bhi.
    private List<RuleSelection> rules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleSelection {
        private String ruleType;      // "EARLY_FIVE", "TOP_LINE", "FULL_HOUSE", etc.
        private Integer order;
        private Integer maxWinners;
        private Integer threshold;    // optional
    }
}