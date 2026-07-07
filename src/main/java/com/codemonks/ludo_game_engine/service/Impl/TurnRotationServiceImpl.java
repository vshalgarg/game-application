package com.codemonks.ludo_game_engine.service.Impl;

import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_game_engine.service.TurnRotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TurnRotationServiceImpl implements TurnRotationService {

    @Override
    public GameStateDTO updateTurn(GameStateDTO gameState, Long currentPlayerId,boolean extraTurn)
    {

       // log.info("Turn  started for playerId:{}", currentPlayerId);
        log.debug("Turn service execution started for playerId:{}", currentPlayerId);

        //Find current player

        PlayerDTO currentPlayer = null;

        for (PlayerDTO player : gameState.getPlayers()) {

            if (player.getPlayerId().equals(currentPlayerId)) {
                currentPlayer = player;
                break;
            }
        }

        if (currentPlayer == null) {log.error("Current player not found:{}", currentPlayerId);
            return gameState;
        }

         //If player still has pending dice,keep turn with same player.
        if (currentPlayer.getPendingDice() != null && !currentPlayer.getPendingDice().isEmpty()) {
            gameState.setCurrentTurnPlayerId(currentPlayerId);
            log.info("[RETAIN_TURN] PlayerId:{}|Pending Dice:{}|Keeping turn to process remaining moves.",
                    currentPlayerId, currentPlayer.getPendingDice());
            return gameState;
        }

        //Kill/Home bonus turn
        if (extraTurn) {
            gameState.setCurrentTurnPlayerId(currentPlayerId);
            log.info("[EXTRA TURN] granted PlayerId:{}", currentPlayerId);
            return gameState;
        }

        //Get all players
        List<PlayerDTO> players = gameState.getPlayers();
        int currentPlayerIndex = -1;
        //Find current player index
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerId().equals(currentPlayerId)) {
                currentPlayerIndex = i;
                break;
            }
        }

        //Safety check

        if (currentPlayerIndex == -1) {
            log.error("Current player not found:{}", currentPlayerId);
            return gameState;
        }
        //Next player
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Long nextPlayerId = players.get(nextPlayerIndex).getPlayerId();
        gameState.setCurrentTurnPlayerId(nextPlayerId);
        return gameState;
    }
}