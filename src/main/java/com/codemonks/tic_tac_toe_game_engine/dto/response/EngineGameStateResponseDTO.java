package com.codemonks.tic_tac_toe_game_engine.dto.response;

import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EngineGameStateResponseDTO {

    private List<List<String>> boardState;
    private Long currentTurnUserId;
    private GameStatusEnum status; // Enum: RUNNING, WIN, DRAW
    private Long winnerUserId;
    private List<PlayerDto> players;

}
