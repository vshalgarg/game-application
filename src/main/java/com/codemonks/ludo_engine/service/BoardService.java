package com.codemonks.ludo_engine.service;

import com.codemonks.ludo_engine.model.BoardLayout;
import com.codemonks.ludo_engine.model.Grid;

import java.util.List;
import java.util.Set;

public interface BoardService {

    BoardLayout getBoard();

    List<String> getColors();
    Grid getCell(Integer cellId);
    List<Integer> getPath(Integer colorIndex);
    Set<Integer> getSafeCells();
}
