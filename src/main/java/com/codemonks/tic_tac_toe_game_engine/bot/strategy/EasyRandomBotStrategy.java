package com.codemonks.tic_tac_toe_game_engine.bot.strategy;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.domain.board.CellValue;
import com.codemonks.tic_tac_toe_game_engine.domain.move.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EasyRandomBotStrategy implements BotStrategy {

    private final Random random = new Random();

    @Override
    public Move chooseMove(Board board, CellValue botSymbol) {

        List<Move> availableMoves = new ArrayList<>();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.isCellEmpty(row, col)) {
                    availableMoves.add(
                            new Move(row, col)
                    );
                }
            }
        }

        return availableMoves.get(
                random.nextInt(availableMoves.size())
        );
    }
}