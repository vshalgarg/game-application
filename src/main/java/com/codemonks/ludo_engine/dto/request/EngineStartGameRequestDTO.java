package com.codemonks.ludo_engine.dto.request;

import com.codemonks.ludo_engine.enums.BotDifficultyEnum;
import com.codemonks.ludo_engine.enums.GameTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngineStartGameRequestDTO {

    //room will be created by game service and engine will only use room info.

    @NotNull(message = "Room id cannot be null")
    private long roomId;

    @NotNull(message = "room code required")
    private String roomCode;

    //List of All joined players
    @NotNull(message = "Players required")
    private List<Long> playerIds;

    @NotNull(message = "Game type is required")
    private GameTypeEnum gameType;

    private BotDifficultyEnum botDifficulty;

}
