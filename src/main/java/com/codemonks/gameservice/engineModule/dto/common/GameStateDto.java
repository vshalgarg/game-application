package com.codemonks.gameservice.engineModule.dto.common;

import com.codemonks.gameservice.enums.GameStatusEnum;
import lombok.Data;

@Data
public class GameStateDto {

    private String boardState;
    private GameStatusEnum status;
}
