package com.codemonks.gameservice.engineModule.dto.realtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RealtimeGameStateDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("game_state_data")
    private Map<String, Object> gameState;

    @JsonProperty("current_turn_user_id")
    private Long currentTurnUserId;

    @JsonProperty("game_status")
    private String gameStatus;

    @JsonProperty("winner_user_id")
    private Long winnerUserId;

    @JsonProperty("version")
    private Long version;

}
