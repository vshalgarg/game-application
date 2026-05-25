package com.codemonks.gameservice.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class MakeMoveRequestDTO {

    private Long userId;
    private Map<String, Object> moveData;
}
