package com.codemonks.gameservice.engineModule.dto.request;

import com.codemonks.gameservice.engineModule.enums.BotDifficultyEnum;
import com.codemonks.gameservice.engineModule.enums.MatchTypeEnum;
import com.codemonks.gameservice.enums.GameTypeEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EngineStartGameRequestDTO {
    private Long roomId;
    private String roomCode;
    private GameTypeEnum gameType;
    private List<Long> playerIds;
    private BotDifficultyEnum botDifficulty;
    private MatchTypeEnum matchType;

    // NAYA — sirf Tambola ke liye populate hote hain (Ludo/TicTacToe
    // ke liye hamesha null rahenge, harmless). Field-names aur types
    // Tambola-engine ke apne EngineStartGameRequestDTO se EXACTLY match
    // karte hain, taaki Feign-call bina kisi mapping-DTO ke directly kaam kare.
    private Integer timerIntervalSeconds;
    private List<TambolaPlayerRequestDTO> players;
    private List<TambolaRuleConfigRequestDTO> rules;


}
