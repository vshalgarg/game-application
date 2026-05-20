package com.codemonks.gameservice.engineModule.dto.request;

import com.codemonks.gameservice.engineModule.dto.common.MoveDataDTO;
import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineMoveRequestDTO {
    private Long roomId;
    private List<List<String>> boardState;
    private Long currentTurnUserId;
    private Long userId;
    private MoveDataDTO moveData;
    private List<PlayerDto> players;
}
