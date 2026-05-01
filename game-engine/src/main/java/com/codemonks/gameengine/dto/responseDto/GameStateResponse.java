package com.codemonks.gameengine.dto.responseDto;

import com.codemonks.gameengine.dto.PlayerDTO;
import com.codemonks.gameengine.enums.GameStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class GameStateResponse {
    private List<List<String>> board; // "X", "O", or null
    private Long currentPlayer;
    private List<PlayerDTO> players;
    private GameStatusEnum status;
    private Long winner;
}
