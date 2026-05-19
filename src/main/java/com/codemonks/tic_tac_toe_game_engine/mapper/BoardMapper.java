package com.codemonks.tic_tac_toe_game_engine.mapper;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;

import java.util.ArrayList;
import java.util.List;

public class BoardMapper {

    public static Board toDomain(List<List<String>> boardState) {

        Board board = new Board();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                String value = boardState.get(row).get(col);
                if (value == null) {
                    board.setCell(row, col, CellValue.EMPTY);
                }
                else {
                    board.setCell(row, col, CellValue.valueOf(value));
                }
            }
        }
        return board;
    }

    public static List<List<String>> toDto(Board board) {

        List<List<String>> boardState = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            List<String> currentRow = new ArrayList<>();
            for (int col = 0; col < 3; col++) {
                CellValue value = board.getCell(row, col);
                if (value == CellValue.EMPTY) {
                    currentRow.add(null);
                }
                else {
                    currentRow.add(value.name());
                }
            }
            boardState.add(currentRow);
        }
        return boardState;
    }
}