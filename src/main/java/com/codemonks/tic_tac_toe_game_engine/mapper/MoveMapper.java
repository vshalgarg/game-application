package com.codemonks.tic_tac_toe_game_engine.mapper;

import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import com.codemonks.tic_tac_toe_game_engine.dto.MoveDataDTO;

public class MoveMapper {

    public static Move toDomain(MoveDataDTO dto) {

        return new Move(
                dto.getRow(),
                dto.getCol()
        );
    }
}