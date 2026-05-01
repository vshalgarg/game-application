package com.codemonks.gameengine.dto.requestDto;

import com.codemonks.gameengine.dto.MovePosition;
import lombok.Data;

@Data
public class MoveRequest {

    private Long playerId;
    private MovePosition position;
}
