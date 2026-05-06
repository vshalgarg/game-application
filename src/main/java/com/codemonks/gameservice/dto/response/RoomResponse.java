package com.codemonks.gameservice.dto.response;

import com.codemonks.gameservice.enums.GameTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {

    private Long roomId;
    private String userId;
    private String roomCode;
    private GameTypeEnum gameType;
    private String status;
}
