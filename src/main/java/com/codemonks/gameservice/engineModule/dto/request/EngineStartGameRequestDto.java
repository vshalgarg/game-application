package com.codemonks.gameservice.engineModule.dto.request;

import com.codemonks.gameservice.enums.GameTypeEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EngineStartGameRequestDto {
    private Long roomId;
    private String roomCode;
    private GameTypeEnum gameType;
    private List<Long> playerIds;
}
