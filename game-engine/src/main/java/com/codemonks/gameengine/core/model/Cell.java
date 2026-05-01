package com.codemonks.gameengine.core.model;

import com.codemonks.gameengine.enums.SymbolEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cell {
    private int row;
    private int col;
    private SymbolEnum symbol;
}
