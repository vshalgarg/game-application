package com.codemonks.gameengine.core.validator.validators;

import com.codemonks.gameengine.core.model.GameState;
import com.codemonks.gameengine.core.model.Player;
import com.codemonks.gameengine.core.validator.MoveValidator;

public class PositionValidator implements MoveValidator {

    @Override
    public void validate(GameState state, int row, int col, Player player) {

        int size = state.getBoard().getSize();

        if (row < 0 || col < 0 || row >= size || col >= size) {
            throw new IllegalArgumentException("Invalid position");
        }
    }
}
