package com.codemonks.ludo_engine.dto.request;

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
