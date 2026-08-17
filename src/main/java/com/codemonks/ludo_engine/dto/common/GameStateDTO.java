package com.codemonks.ludo_engine.dto.common;

import com.codemonks.ludo_engine.enums.GameStatusEnum;
import com.codemonks.ludo_engine.enums.PlayerTurnStageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDTO {

    private Long currentTurnPlayerId;
    private GameStatusEnum gameStatus;
    private List<PlayerDTO>players;
    private PlayerTurnStageEnum playerTurnStage;
    private Long winnerPlayerId;
    private List<EventDTO> events;
    private Integer lastDice;
    private Long lastDicePlayerId;
    private List<LegalMoveDTO> legalMoves;
    private List<Long> finishOrder = new ArrayList<>();
}
