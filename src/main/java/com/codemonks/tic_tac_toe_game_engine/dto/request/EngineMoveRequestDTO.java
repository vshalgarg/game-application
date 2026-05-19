package com.codemonks.tic_tac_toe_game_engine.dto.request;

import com.codemonks.tic_tac_toe_game_engine.dto.MoveDataDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EngineMoveRequestDTO {

    private Long gameId;
    private List<List<String>> boardState;
    private Long currentTurnUserId;
    private Long userId;
    private MoveDataDTO moveData;
    private List<PlayerDto> players;
}
