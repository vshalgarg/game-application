package com.codemonks.tic_tac_toe_game_engine.domain.board;

import lombok.Getter;

@Getter
public class Board {

    private final CellValue[][] cells;

    public Board() {

        this.cells = new CellValue[3][3];
        initializeBoard();
    }

    private void initializeBoard() {

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col] = CellValue.EMPTY;
            }
        }
    }

    public CellValue getCell(int row, int col) {
        return cells[row][col];
    }

    public void setCell(int row, int col, CellValue value) {
        cells[row][col] = value;
    }

    public boolean isCellEmpty(int row, int col) {
        return cells[row][col] == CellValue.EMPTY;
    }

    public boolean isBoardFull() {

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (cells[row][col] == CellValue.EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean checkWin(CellValue symbol) {

        for (int i = 0; i < 3; i++) {
            // Check rows
            if (cells[i][0] == symbol &&
                    cells[i][1] == symbol &&
                    cells[i][2] == symbol) {

                return true;
            }

            // Check columns
            if (cells[0][i] == symbol &&
                    cells[1][i] == symbol &&
                    cells[2][i] == symbol) {

                return true;
            }
        }

        // Main diagonal
        if (cells[0][0] == symbol &&
                cells[1][1] == symbol &&
                cells[2][2] == symbol) {

            return true;
        }

        // Opposite diagonal
        return cells[0][2] == symbol &&
                cells[1][1] == symbol &&
                cells[2][0] == symbol;
    }
}