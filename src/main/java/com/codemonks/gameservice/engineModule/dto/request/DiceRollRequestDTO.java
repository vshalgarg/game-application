package com.codemonks.gameservice.engineModule.dto.request;

import lombok.*;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiceRollRequestDTO {
    private Long roomId;
    private Long playerId;

}