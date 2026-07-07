package com.codemonks.ludo_game_engine.dto.common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KillRuleResultDTO {

        private GameStateDTO gameState;
        private boolean tokenKilled;
        private Long killedPlayerId;
        private Long killedTokenId;

}
