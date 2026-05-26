package com.codemonks.gameservice.engineModule.dto.realtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RealtimeMoveDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("player_id")
    private Long playerId;

    @JsonProperty("move_data")
    private Map<String, Object> moveData;

}
