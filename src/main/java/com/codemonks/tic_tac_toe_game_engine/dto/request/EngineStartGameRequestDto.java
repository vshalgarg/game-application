package com.codemonks.tic_tac_toe_game_engine.dto.request;

import com.codemonks.tic_tac_toe_game_engine.enums.GameTypeEnum;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class EngineStartGameRequestDto {

    private Long roomId;
    private String roomCode;
    private GameTypeEnum gameType;
    private List<Long> playerIds;

}
