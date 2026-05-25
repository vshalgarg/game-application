package com.codemonks.tic_tac_toe_game_engine.bot.strategy;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class EasyRandomBotStrategy implements BotStrategy {

    private final Random random = new Random();

    @Override
    public Move chooseMove(Board board, CellValue botSymbol) {
        List<Move> emptyCells = getEmptyCells(board);

        if (emptyCells.isEmpty()) {
            throw new IllegalStateException("No valid moves available");
        }

        // Picks a completely random empty cell — no intelligence
        return emptyCells.get(random.nextInt(emptyCells.size()));
    }

    private List<Move> getEmptyCells(Board board) {
        List<Move> cells = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.isCellEmpty(r, c)) {
                    cells.add(new Move(r, c));
                }
            }
        }
        return cells;
    }
}