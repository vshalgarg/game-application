package com.codemonks.tic_tac_toe_game_engine.bot.strategy;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MediumBotStrategy implements BotStrategy {

    @Override
    public Move chooseMove(Board board, CellValue botSymbol) {
        CellValue opponent = botSymbol == CellValue.X ? CellValue.O : CellValue.X;

        Move winMove = findWinningMove(board, botSymbol);
        if (winMove != null) return winMove;

        Move blockMove = findWinningMove(board, opponent);
        if (blockMove != null) return blockMove;

        // Take center — strategically strongest position
        if (board.isCellEmpty(1, 1)) return new Move(1, 1);

        // Take any corner — second strongest positions
        for (int[] corner : new int[][]{{0, 0}, {0, 2}, {2, 0}, {2, 2}}) {
            if (board.isCellEmpty(corner[0], corner[1])) {
                return new Move(corner[0], corner[1]);
            }
        }

        // Any remaining empty cell
        return getAnyEmpty(board);
    }

    private Move findWinningMove(Board board, CellValue symbol) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (!board.isCellEmpty(r, c)) continue;

                board.setCell(r, c, symbol);           // try move
                boolean wins = board.checkWin(symbol);
                board.setCell(r, c, CellValue.EMPTY);  // undo move

                if (wins) return new Move(r, c);
            }
        }
        return null;
    }

    private Move getAnyEmpty(Board board) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.isCellEmpty(r, c)) return new Move(r, c);
            }
        }
        throw new IllegalStateException("No valid moves available — board is full");
    }
}
