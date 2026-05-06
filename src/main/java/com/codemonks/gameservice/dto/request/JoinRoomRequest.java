package com.codemonks.gameservice.dto.request;

import lombok.Data;

@Data
public class JoinRoomRequest {

    private String tenantId;
    private Long userId;
}
