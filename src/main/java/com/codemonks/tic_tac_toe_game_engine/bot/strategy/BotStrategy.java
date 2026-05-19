package com.codemonks.tic_tac_toe_game_engine.bot.strategy;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;

public interface BotStrategy {

    Move chooseMove(Board board, CellValue botSymbol);
}
