package com.codemonks.tic_tac_toe_game_engine.dto.request;

import com.codemonks.tic_tac_toe_game_engine.bot.enums.BotDifficultyEnum;
import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EngineMoveRequestDTO {

    private Long roomId;
    private Map<String, Object> gameState;
    private Long currentTurnUserId;
    private Long userId;
    private Map<String, Object> moveData;
    private List<PlayerDto> players;
    private BotDifficultyEnum botDifficulty;
}
