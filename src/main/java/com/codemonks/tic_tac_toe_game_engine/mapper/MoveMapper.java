package com.codemonks.tic_tac_toe_game_engine.mapper;

import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;

import java.util.Map;

public class MoveMapper {
    private MoveMapper() {}

    public static Move toDomain(
            Map<String, Object> moveData
    ) {

        return new Move(
                (Integer) moveData.get("row"),
                (Integer) moveData.get("col")
        );
    }
}
