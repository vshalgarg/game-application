package com.codemonks.gameservice.dto.request;

import lombok.Data;

@Data
public class JoinRoomRequestDTO {

    private String tenantId;
    private Long userId;
}
