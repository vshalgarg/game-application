package com.codemonks.gameengine.core.model;

import com.codemonks.gameengine.enums.SymbolEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    private Cell[][] grid;
    private int size;

    public boolean isCellEmpty(int row, int col) {
        return grid[row][col].getSymbol() == null;
    }

    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell(i, j, null);
            }
        }
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].getSymbol() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void placeMove(int row, int col, SymbolEnum symbol) {
        grid[row][col].setSymbol(symbol);
    }

}
