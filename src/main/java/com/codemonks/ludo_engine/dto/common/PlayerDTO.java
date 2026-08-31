package com.codemonks.ludo_engine.dto.common;

import com.codemonks.ludo_engine.enums.BotDifficultyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {
    private Long playerId;
    private Integer colorIndex;
    private List<TokenDTO> tokens;
    private List<Integer> pendingDice;
    private Boolean isBot;
    private BotDifficultyEnum botDifficulty;
    private Boolean pendingExtraTurn;
    private Boolean hasFinished;
    private Integer consecutiveSixCount;
}
