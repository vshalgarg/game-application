package com.codemonks.gameservice.engineModule.strategy;

import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.request.DiceRollRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.DiceRollResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.enums.GameTypeEnum;

public interface GameEngine {

    GameTypeEnum supports();
    EngineGameStateResponseDTO startGame(EngineStartGameRequestDTO request);
    EngineGameStateResponseDTO processMove(EngineMoveRequestDTO request);
    default DiceRollResponseDTO rollDice(DiceRollRequestDTO request) {
       throw new UnsupportedOperationException(supports().name() + " does not support dice roll");
   }
    default void publishLobbyState(RealtimeLobbyDTO lobbyDTO) {
        throw new UnsupportedOperationException(supports().name() + " does not support lobby publish");
    }

}
