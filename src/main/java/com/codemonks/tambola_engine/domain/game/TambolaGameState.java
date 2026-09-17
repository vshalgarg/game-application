package com.codemonks.tambola_engine.domain.game;

import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TambolaGameState {

    private Long roomId;
    private String roomCode;
    private GameStatusEnum status;
    private List<Integer> calledNumbers;
    private Integer timerIntervalSeconds;
    private Instant nextTickAt;
    private List<PlayerDTO> players;
     private Long version;
}