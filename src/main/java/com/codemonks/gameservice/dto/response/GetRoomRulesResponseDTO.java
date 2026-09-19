package com.codemonks.gameservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRoomRulesResponseDTO {

    private List<RoomRule> rules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomRule {
        private String ruleType;
        private Integer order;
        private Integer threshold;
    }
}