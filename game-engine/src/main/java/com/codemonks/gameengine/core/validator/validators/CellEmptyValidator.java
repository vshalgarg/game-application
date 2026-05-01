package com.codemonks.gameengine.core.validator.validators;

import com.codemonks.gameengine.core.model.GameState;
import com.codemonks.gameengine.core.model.Player;
import com.codemonks.gameengine.core.validator.MoveValidator;

public class CellEmptyValidator implements MoveValidator {

    @Override
    public void validate(GameState state, int row, int col, Player player) {

        if (!state.getBoard().isCellEmpty(row, col)) {
            throw new IllegalStateException("Cell already occupied");
        }
    }
}
