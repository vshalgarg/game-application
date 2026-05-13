package com.codemonks.gameservice.engineModule.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RealtimeMoveDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("move_number")
    private Integer moveNumber;

    @JsonProperty("player_id")
    private Long playerId;

    @JsonProperty("move_data")
    private Map<String, Object> moveData;
}
