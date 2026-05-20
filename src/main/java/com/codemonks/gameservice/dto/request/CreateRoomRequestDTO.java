package com.codemonks.gameservice.dto.request;

import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import lombok.Data;

@Data
public class CreateRoomRequestDTO {

    private String tenantId;
    private Long userId;
    private GameTypeEnum gameType;
}
