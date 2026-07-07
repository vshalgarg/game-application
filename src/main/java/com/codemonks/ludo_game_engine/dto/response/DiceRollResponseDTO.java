package com.codemonks.ludo_game_engine.dto.response;

import com.codemonks.ludo_game_engine.enums.PlayerTurnStageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiceRollResponseDTO {

    private Long roomId;
    private Long playerId;
    private Integer dice;
    private List<Integer> pendingDice;

    private Map<String, Object> gameState;
    private boolean tripleSixForfeited;
    private Long currentTurnPlayerId;
    private PlayerTurnStageEnum playerTurnStage;

}
