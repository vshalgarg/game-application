package com.codemonks.tic_tac_toe_game_engine.bot.minimax;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import org.springframework.stereotype.Component;

@Component
public class MinimaxEngine {

    public Move getBestMove(Board board, CellValue botSymbol) {
        CellValue opponent = botSymbol == CellValue.X ? CellValue.O : CellValue.X;

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (!board.isCellEmpty(r, c)) continue;

                board.setCell(r, c, botSymbol);   // try
                int score = minimax(board, 0, false, botSymbol, opponent);
                board.setCell(r, c, CellValue.EMPTY); // undo

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(r, c);
                }
            }
        }

        return bestMove;
    }

    private int minimax(Board board, int depth, boolean isMaximizing,
                        CellValue botSymbol, CellValue opponent) {

        // Terminal state checks — uses YOUR existing Board methods
        if (board.checkWin(botSymbol))  return 10 - depth; // bot wins, faster = better
        if (board.checkWin(opponent))   return depth - 10; // bot loses, faster = worse
        if (board.isBoardFull())        return 0;          // draw

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (!board.isCellEmpty(r, c)) continue;
                    board.setCell(r, c, botSymbol);
                    int score = minimax(board, depth + 1, false, botSymbol, opponent);
                    board.setCell(r, c, CellValue.EMPTY);
                    best = Math.max(best, score);
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (!board.isCellEmpty(r, c)) continue;
                    board.setCell(r, c, opponent);
                    int score = minimax(board, depth + 1, true, botSymbol, opponent);
                    board.setCell(r, c, CellValue.EMPTY);
                    best = Math.min(best, score);
                }
            }
            return best;
        }
    }
}
