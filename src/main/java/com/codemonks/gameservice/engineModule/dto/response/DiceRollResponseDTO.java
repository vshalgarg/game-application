package com.codemonks.gameservice.engineModule.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiceRollResponseDTO {

    @JsonProperty("roomId")
    private Long roomId;

    @JsonProperty("playerId")
    private Long playerId;

    @JsonProperty("dice")
    private Integer dice;

    @JsonProperty("pendingDice")
    private List<Integer> pendingDice;

    @JsonProperty("gameState")
    private Map<String, Object> gameState;

    @JsonProperty("tripleSixForfeited")
    private boolean tripleSixForfeited;

    @JsonProperty("currentTurnPlayerId")
    private Long currentTurnPlayerId;

    @JsonProperty("playerTurnStage")
    private String playerTurnStage; // ← String, not enum
}