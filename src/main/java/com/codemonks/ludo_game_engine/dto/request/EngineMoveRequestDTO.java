package com.codemonks.ludo_game_engine.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngineMoveRequestDTO {

    private Long roomId;

    private String roomCode;//added

    private Map<String, Object> gameState;

    private Long currentTurnUserId;

    private Long userId;

    private Map<String, Object> moveData;

}
