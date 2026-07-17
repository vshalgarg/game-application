package com.codemonks.ludo_engine.dto.common;

import com.codemonks.ludo_engine.enums.GameStatusEnum;
import com.codemonks.ludo_engine.enums.PlayerTurnStageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDTO {

    private Long currentTurnPlayerId;//engine will validate move from current turn PlayerId
    private GameStatusEnum gameStatus; //WAITING,RUNNING,FINISHED
    private List<PlayerDTO>players; //Engine needs all players because:
    // Kill rules ,Win checks,Safe zones,depend on everybody.

    private PlayerTurnStageEnum playerTurnStage;
    // Winner information
    private Long winnerPlayerId;

    private Integer lastDice;

    private Long lastDicePlayerId;
}
