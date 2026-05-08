package com.codemonks.gameservice.engineModule.dto.response;

import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.enums.GameStatusEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineGameStateResponseDTO {

    private List<List<String>> boardState;
    private Long currentTurnUserId;
    private GameStatusEnum status;
    private Long winnerUserId;
    private List<PlayerDto> players;
}
