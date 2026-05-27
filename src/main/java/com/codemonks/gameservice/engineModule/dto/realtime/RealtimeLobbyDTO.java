package com.codemonks.gameservice.engineModule.dto.realtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RealtimeLobbyDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("room_code")
    private String roomCode;

    @JsonProperty("room_status")
    private String roomStatus;

    @JsonProperty("players")
    private List<LobbyPlayerDTO> players;
}
