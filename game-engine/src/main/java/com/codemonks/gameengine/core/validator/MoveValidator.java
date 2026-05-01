package com.codemonks.gameengine.core.validator;

import com.codemonks.gameengine.core.model.GameState;
import com.codemonks.gameengine.core.model.Player;

public interface MoveValidator {

    void validate(GameState state, int row, int col, Player player);
}
