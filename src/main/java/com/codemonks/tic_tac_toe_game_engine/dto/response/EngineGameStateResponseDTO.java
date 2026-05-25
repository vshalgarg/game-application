package com.codemonks.tic_tac_toe_game_engine.dto.response;

import com.codemonks.tic_tac_toe_game_engine.bot.enums.BotDifficultyEnum;
import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EngineGameStateResponseDTO {

    private Map<String, Object> gameState;
    private Long currentTurnUserId;
    private GameStatusEnum status; // Enum: RUNNING, WIN, DRAW
    private Long winnerUserId;
    private List<PlayerDto> players;
    private BotDifficultyEnum botDifficulty;
}
