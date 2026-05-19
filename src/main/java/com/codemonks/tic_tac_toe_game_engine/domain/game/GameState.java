package com.codemonks.tic_tac_toe_game_engine.domain.game;

import com.codemonks.tic_tac_toe_game_engine.domain.board.Board;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameState {

    private Board board;
    private Long currentTurnUserId;
    private GameStatusEnum status;
    private Long winnerUserId;

}