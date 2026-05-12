package com.codemonks.gameservice.dto.response;

import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoomDetailsResponseDTO {

    private Long roomId;
    private String tenantId;
    private String roomCode;
    private GameTypeEnum gameType;
    private RoomStatusEnum status;

    private List<PlayerResponseDTO> players;
}
