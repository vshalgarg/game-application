package com.codemonks.gameservice.engineModule.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeGameStateDTO {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("room_code")
    private String roomCode;

    @JsonProperty("board_state")
    private List<List<String>> boardState;

    @JsonProperty("current_turn_user_id")
    private Long currentTurnUserId;

    @JsonProperty("game_state")
    private String gameState;

    @JsonProperty("winner_user_id")
    private Long winnerUserId;

    @JsonProperty("players")
    private List<PlayerDto> players;
}