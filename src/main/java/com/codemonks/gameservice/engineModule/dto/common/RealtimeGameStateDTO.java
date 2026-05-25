package com.codemonks.gameservice.engineModule.dto.common;

import com.codemonks.gameservice.engineModule.enums.BotDifficultyEnum;
import com.codemonks.gameservice.engineModule.enums.MatchTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RealtimeGameStateDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("room_code")
    private String roomCode;

    @JsonProperty("game_state_data")
    private Map<String, Object> gameState;

    @JsonProperty("current_turn_user_id")
    private Long currentTurnUserId;

    @JsonProperty("game_status")
    private String gameStatus;

    @JsonProperty("winner_user_id")
    private Long winnerUserId;

    @JsonProperty("players")
    private List<PlayerDto> players;

    @JsonProperty("match_type")
    private MatchTypeEnum matchType;

    @JsonProperty("bot_difficulty")
    private BotDifficultyEnum botDifficulty;
}