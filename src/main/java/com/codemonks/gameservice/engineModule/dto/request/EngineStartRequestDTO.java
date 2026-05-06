package com.codemonks.gameservice.engineModule.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EngineStartRequestDTO {
    private Long roomId;
    private List<Long> playerIds;
}
