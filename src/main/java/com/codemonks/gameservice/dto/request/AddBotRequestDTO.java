package com.codemonks.gameservice.dto.request;


import com.codemonks.gameservice.engineModule.enums.BotDifficultyEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AddBotRequestDTO {

    @NotNull
    private String tenantId;
    @NotNull
    private Long hostUserId;
    private BotDifficultyEnum botDifficulty;

}