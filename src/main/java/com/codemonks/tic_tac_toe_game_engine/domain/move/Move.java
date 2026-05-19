package com.codemonks.tic_tac_toe_game_engine.domain.move;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Move {

    private int row;
    private int col;

}