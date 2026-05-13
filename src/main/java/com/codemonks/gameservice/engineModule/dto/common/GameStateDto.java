package com.codemonks.gameservice.engineModule.dto.common;

import com.codemonks.gameservice.enums.RoomStatusEnum;
import lombok.Data;

@Data
public class GameStateDto {

    private String boardState;
    private RoomStatusEnum status;
}
