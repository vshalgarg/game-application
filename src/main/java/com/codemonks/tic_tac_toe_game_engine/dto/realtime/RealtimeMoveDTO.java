package com.codemonks.tic_tac_toe_game_engine.dto.realtime;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RealtimeMoveDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("room_code")
    private String roomCode;

    @JsonProperty("player_id")
    private Long playerId;

    @JsonProperty("move_data")
    private Map<String, Object> moveData;

}
