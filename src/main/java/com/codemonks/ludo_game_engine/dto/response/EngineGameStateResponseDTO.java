package com.codemonks.ludo_game_engine.dto.response;


import com.codemonks.ludo_game_engine.dto.common.EventDTO;
import com.codemonks.ludo_game_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_game_engine.enums.BotDifficultyEnum;
import com.codemonks.ludo_game_engine.enums.GameStatusEnum;
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

    private List<PlayerDTO> players;

    private BotDifficultyEnum botDifficulty;
}