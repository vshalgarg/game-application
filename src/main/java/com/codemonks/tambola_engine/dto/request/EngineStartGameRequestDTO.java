package com.codemonks.tambola_engine.dto.request;

import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Sent by game-service to the engine to start a new Tambola game.
 * Carries everything the engine needs to initialize TambolaGameState:
 * the room, its players, the timer interval, and which claim rules
 * are active for this game.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngineStartGameRequestDTO {
    private Long roomId;
    private Integer timerIntervalSeconds;
    private List<PlayerDTO> players;
    private List<RuleConfigRequestDTO> rules;
}