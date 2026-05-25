package com.codemonks.gameservice.engineModule.dto.response;

import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.engineModule.enums.BotDifficultyEnum;
import com.codemonks.gameservice.engineModule.enums.GameStatusEnum;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineGameStateResponseDTO {

    private Map<String, Object> gameState;
    private Long currentTurnUserId;
    private GameStatusEnum status;
    private Long winnerUserId;
    private List<PlayerDto> players;
    private BotDifficultyEnum botDifficulty;
}
