package com.codemonks.gameservice.dto.request;

import com.codemonks.gameservice.enums.GameTypeEnum;
import lombok.Data;

@Data
public class CreateRoomRequest {

    private String tenantId;
    private Long userId;
    private GameTypeEnum gameType;
}
