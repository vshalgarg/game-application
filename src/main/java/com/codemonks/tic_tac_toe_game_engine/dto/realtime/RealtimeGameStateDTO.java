package com.codemonks.tic_tac_toe_game_engine.dto.realtime;

import com.codemonks.tic_tac_toe_game_engine.bot.enums.BotDifficultyEnum;
import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private List<PlayerDTO> players;

    @JsonProperty("bot_difficulty")
    private BotDifficultyEnum botDifficulty;

    @JsonProperty("state_sequence")
    private Long stateSequence;
}