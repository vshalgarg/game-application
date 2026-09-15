package com.codemonks.tambola_engine.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// GameSetupService.initializeGame() ka return type.

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSetupResult {

    private Long roomId;

    // Ab dynamic hai (state.getStatus().name() se aata hai
    // GameSetupServiceImpl me) - hamesha "INITIALIZED" nahi hoga,
    // Option-A design ke hisaab se seedha "RUNNING" ho sakta hai.
    private String status;


    private Integer totalTicketsGenerated;
}