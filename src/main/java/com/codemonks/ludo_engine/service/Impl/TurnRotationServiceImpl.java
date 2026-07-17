package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.constant.BoardConstants;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.enums.PlayerColorEnum;
import com.codemonks.ludo_engine.service.TurnRotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TurnRotationServiceImpl implements TurnRotationService {

    @Override
    public GameStateDTO updateTurn(GameStateDTO gameState, Long currentPlayerId, boolean extraTurn)
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

        List<PlayerDTO> players = gameState.getPlayers();

        PlayerColorEnum currentColor = currentPlayer.getColor();
        int currentColorIndex = BoardConstants.TURN_ORDER.indexOf(currentColor);

        if (currentColorIndex == -1) {
            log.error("Current player's color not found in TURN_ORDER: {}", currentColor);
            return gameState;
        }

        Long nextPlayerId = null;
        int size = BoardConstants.TURN_ORDER.size();

        for (int step = 1; step <= size; step++) {
            PlayerColorEnum candidateColor = BoardConstants.TURN_ORDER.get((currentColorIndex + step) % size);

            for (PlayerDTO player : players) {
                if (player.getColor() == candidateColor) {
                    nextPlayerId = player.getPlayerId();
                    break;
                }
            }
            if (nextPlayerId != null) {
                break;
            }
        }
        if (nextPlayerId == null) {
            log.error("No next player found in clockwise rotation. CurrentColor:{}", currentColor);
            return gameState;
        }
        gameState.setCurrentTurnPlayerId(nextPlayerId);
        log.info("[TURN_ROTATED] From:{}({}) To:{}", currentPlayerId, currentColor, nextPlayerId);
        return gameState;
}}