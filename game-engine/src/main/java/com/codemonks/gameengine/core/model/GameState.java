package com.codemonks.gameengine.core.model;

import com.codemonks.gameengine.enums.GameStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class GameState {

    private Board board;
    private List<Player> players;
    private Player currentPlayer;
    private GameStatusEnum status;
    private Player winner;
}
