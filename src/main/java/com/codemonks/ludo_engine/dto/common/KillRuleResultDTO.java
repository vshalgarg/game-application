package com.codemonks.ludo_engine.dto.common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KillRuleResultDTO {

        private GameStateDTO gameState;
        private boolean tokenKilled;
        private Long killedPlayerId;
        private Long killedTokenId;
        private List<Integer> killedTokenJourney;

}
