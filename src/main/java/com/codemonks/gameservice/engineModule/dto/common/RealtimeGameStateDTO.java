package com.codemonks.gameservice.engineModule.dto.common;

import com.codemonks.gameservice.enums.GameStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeGameStateDTO {
    @JsonProperty("game_id")
    private Long gameId;

    @JsonProperty("room_code")
    private String roomCode;

    @JsonProperty("board_state")
    private List<List<String>> boardState;

    @JsonProperty("current_turn_user_id")
    private Long currentTurnUserId;

    @JsonProperty("status")
    private GameStatusEnum status;

    @JsonProperty("winner_user_id")
    private Long winnerUserId;

    @JsonProperty("players")
    private List<PlayerDto> players;
}
