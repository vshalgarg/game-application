package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.exception.InvalidMoveException;
import com.codemonks.ludo_engine.service.TurnRotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.codemonks.ludo_engine.constant.ErrorCodesEnum.INVALID_MOVE;

@Service
@Slf4j
public class TurnRotationServiceImpl implements TurnRotationService {

    @Override
    public GameStateDTO updateTurn(GameStateDTO gameState, Long currentPlayerId, boolean extraTurn)
    {

        log.info(
                "[TURN_ROTATION_STARTED] Player:{} ExtraTurn:{}",
                currentPlayerId,
                extraTurn
        );
        //Find current player
        PlayerDTO currentPlayer = null;
        for (PlayerDTO player : gameState.getPlayers()) {
            if (player.getPlayerId().equals(currentPlayerId)) {
                currentPlayer = player;
                break;
            }
        }

        if (currentPlayer == null) {

            log.error(
                    "[CURRENT_PLAYER_NOT_FOUND] Player:{}",
                    currentPlayerId
            );

            throw new InvalidMoveException(INVALID_MOVE);
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

        int currentPlayerIndex = -1;

        for (int i = 0; i < players.size(); i++) {

            if (players.get(i).getPlayerId().equals(currentPlayerId)) {
                currentPlayerIndex = i;
                break;
            }
        }

        if (currentPlayerIndex == -1) {

            log.error(
                    "[CURRENT_PLAYER_NOT_FOUND] Player:{}",
                    currentPlayerId
            );

            throw new InvalidMoveException(INVALID_MOVE);
        }

        int nextPlayerIndex =
                (currentPlayerIndex + 1) % players.size();

        Long nextPlayerId =
                players.get(nextPlayerIndex).getPlayerId();

        gameState.setCurrentTurnPlayerId(nextPlayerId);

        log.info(
                "[TURN_ROTATED] FromPlayer:{} ToPlayer:{}",
                currentPlayerId,
                nextPlayerId
        );

        return gameState;
}}