package com.codemonks.ludo_engine.dto.realtime;

import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RealtimeGameStateDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("room_code")
    private String roomCode;

    @JsonProperty("game_state_data")
    private Map<String, Object> gameState;

    @JsonProperty("players")
    private List<PlayerDTO> players;

    @JsonProperty("current_turn_user_id")
    private Long currentTurnUserId;

    @JsonProperty("game_status")
    private String gameStatus;

    @JsonProperty("winner_user_id")
    private Long winnerUserId;

    @JsonProperty("version")
    private Long version;

    @JsonProperty("bot_difficulty")
    private String botDifficulty;
}