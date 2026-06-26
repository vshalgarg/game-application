package com.codemonks.gameservice.engineModule.dto.request;

import com.codemonks.gameservice.engineModule.dto.common.PlayerDTO;
import com.codemonks.gameservice.engineModule.enums.BotDifficultyEnum;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineMoveRequestDTO {
    private Long roomId;
   // private Map<String, Object> gameState;
    //private Long currentTurnUserId;
    private String roomCode; // added new
    private Long userId;
    private Map<String, Object> moveData;
    private List<PlayerDTO> players;
    private BotDifficultyEnum botDifficulty;
}
