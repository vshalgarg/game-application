package com.codemonks.gameservice.engineModule.dto.request;

import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
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
    private Map<String, Object> gameState;
    private Long currentTurnUserId;
    private Long userId;
    private Map<String, Object> moveData;
    private List<PlayerDto> players;
    private BotDifficultyEnum botDifficulty;
}
