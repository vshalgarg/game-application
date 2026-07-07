package com.codemonks.ludo_game_engine.dto.request;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiceRollRequestDTO {
    private Long playerId;
    private Long roomId;

}
