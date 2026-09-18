package com.codemonks.tambola_engine.dto.request;

import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngineStartGameRequestDTO {
    private Long roomId;
    private String roomCode;
    private Integer timerIntervalSeconds;
    private List<PlayerDTO> players;
    private List<RuleConfigRequestDTO> rules;
}