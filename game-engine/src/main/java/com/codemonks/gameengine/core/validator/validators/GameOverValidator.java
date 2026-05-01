package com.codemonks.gameengine.core.validator.validators;

import com.codemonks.gameengine.core.model.GameState;
import com.codemonks.gameengine.core.model.Player;
import com.codemonks.gameengine.core.validator.MoveValidator;
import com.codemonks.gameengine.enums.GameStatusEnum;

public class GameOverValidator implements MoveValidator {
    @Override
    public void validate(GameState state, int row, int col, Player player) {
        if (state.getStatus() != GameStatusEnum.IN_PROGRESS) {
            throw new IllegalStateException("Game already finished");
        }
    }
}
