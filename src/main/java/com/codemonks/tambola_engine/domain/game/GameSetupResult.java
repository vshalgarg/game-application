package com.codemonks.tambola_engine.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSetupResult {

    private Long roomId;
    private String status;
    private Integer totalTicketsGenerated;
}