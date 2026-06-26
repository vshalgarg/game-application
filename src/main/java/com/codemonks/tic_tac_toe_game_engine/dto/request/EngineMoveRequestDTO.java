package com.codemonks.tic_tac_toe_game_engine.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EngineMoveRequestDTO {

    private Long roomId;
    private Map<String, Object> moveData;
    private Long userId;
}
