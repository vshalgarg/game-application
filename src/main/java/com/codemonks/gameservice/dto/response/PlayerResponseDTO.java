package com.codemonks.gameservice.dto.response;

import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomPlayerStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerResponseDTO {

    private Long userId;
    private RoomPlayerRole role;
    private RoomPlayerStatus status;
}
