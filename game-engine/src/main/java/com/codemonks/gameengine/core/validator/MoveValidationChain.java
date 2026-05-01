package com.codemonks.gameengine.core.validator;

import com.codemonks.gameengine.core.model.GameState;
import com.codemonks.gameengine.core.model.Player;

import java.util.List;


public class MoveValidationChain {

    private List<MoveValidator> validators;

    public MoveValidationChain(List<MoveValidator> validators) {
        this.validators = validators;
    }

    public void validate(GameState state, int row, int col, Player player) {
        for (MoveValidator v : validators) {
            v.validate(state, row, col, player);
        }
    }
}
