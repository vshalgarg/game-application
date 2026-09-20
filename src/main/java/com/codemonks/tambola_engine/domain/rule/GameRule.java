package com.codemonks.tambola_engine.domain.rule;

import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRule {

    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("rule_type")
    private RuleTypeEnum ruleType;
    @JsonProperty("rule_order")
    private Integer order;
    @JsonIgnore
    private Boolean claimed;
    @JsonProperty("max_winners")
    private Integer maxWinners;
    @JsonProperty("winner_player_ids")
    private List<Long> winnerPlayerIds;
    private Integer threshold;
    private Long version;
    public boolean hasOpenSlots() {
        return winnerPlayerIds.size() < maxWinners;
    }
}