package com.codemonks.ludo_game_engine.dto.realtime;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
