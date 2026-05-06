package com.codemonks.gameservice.engineModule.dto.request;

import lombok.Data;

@Data
public class EngineMoveRequestDTO {
    private Long roomId;
    private Long playerId;
    private Object movePayload;
}
