package com.codemonks.tic_tac_toe_game_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveDataDTO {

    private int row;
    private int col;

}