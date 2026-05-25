package com.codemonks.tic_tac_toe_game_engine.mapper;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardMapper {

    private BoardMapper() {}

    @SuppressWarnings("unchecked")
    public static Board toDomain(Map<String, Object> gameState) {

        Board board = new Board();
        List<List<String>> boardData = (List<List<String>>) gameState.get("board");

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                String value = boardData.get(row).get(col);
                if (value != null && !value.isBlank() && !"EMPTY".equals(value)) {
                    board.setCell(
                            row,
                            col,
                            CellValue.valueOf(value)
                    );
                }
            }
        }
        return board;
    }

    public static Map<String, Object> toMap(Board board) {

        Map<String, Object> response = new HashMap<>();
        List<List<String>> boardData = new ArrayList<>();

        for (int row = 0; row < 3; row++) {
            List<String> currentRow = new ArrayList<>();
            for (int col = 0; col < 3; col++) {
                CellValue value = board.getCell(row, col);
                currentRow.add(
                        value == CellValue.EMPTY
                                ? ""
                                : value.name()
                );
            }
            boardData.add(currentRow);
        }
        response.put("board", boardData);
        return response;
    }
}