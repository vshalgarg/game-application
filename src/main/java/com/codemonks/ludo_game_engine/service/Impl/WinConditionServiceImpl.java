package com.codemonks.ludo_game_engine.service.Impl;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_game_engine.dto.common.TokenDTO;
import com.codemonks.ludo_game_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.ludo_game_engine.enums.GameStatusEnum;
import com.codemonks.ludo_game_engine.enums.TokenStateEnum;
import com.codemonks.ludo_game_engine.service.WinConditionService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WinConditionServiceImpl implements WinConditionService {

    @Override
    public EngineGameStateResponseDTO checkWinner(GameStateDTO gameState, Long playerId) {

        log.info("Winner checking started for Player:{}", playerId);

        EngineGameStateResponseDTO response = new EngineGameStateResponseDTO();

        for (PlayerDTO player : gameState.getPlayers()) {

            if (!player.getPlayerId().equals(playerId)) {
                continue; // ✅ FIX: pehle sirf `break` tha jo pehli
            }             //         iteration mein hi loop tod deta tha
            //         chahe player match hua ya nahi.
            //         Ab `continue` se sirf non-matching
            //         players skip hote ha

            int finishedTokens = 0;

            for (TokenDTO token : player.getTokens()) {
                if (token.getState() == TokenStateEnum.FINISHED) {
                    finishedTokens++;
                }
            }

            log.info("Player:{} FinishedTokens:{}", playerId, finishedTokens);

            // Saare 4 tokens FINISHED → player wins
            if (finishedTokens == 4) {
                response.setWinnerUserId(playerId);
                response.setStatus(GameStatusEnum.WIN);

                // GameState mein bhi winner set karo
                gameState.setWinnerPlayerId(playerId);
                gameState.setGameStatus(GameStatusEnum.FINISHED);

                log.info("[WINNER] Player:{} has won the game!", playerId);
            }

            break; // ✅ player mil gaya — loop se bahar
        }

        log.info("Winner checking completed");
        return response;
    }
}