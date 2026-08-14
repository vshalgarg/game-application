package com.codemonks.tic_tac_toe_game_engine.bot.strategy;

import com.codemonks.tic_tac_toe_game_engine.bot.minimax.MinimaxEngine;
import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class HardBotStrategy implements BotStrategy {

    private final MinimaxEngine minimaxEngine;

    public HardBotStrategy(MinimaxEngine minimaxEngine) {
        this.minimaxEngine = minimaxEngine;
    }

    @Override
    public Move chooseMove(Board board, CellValue botSymbol) {
        // Delegates entirely to minimax — guaranteed optimal play
        return minimaxEngine.getBestMove(board, botSymbol);
    }
}
