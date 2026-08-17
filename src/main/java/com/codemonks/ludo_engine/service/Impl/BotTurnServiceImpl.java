package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.ludo_engine.service.BotMoveService;
import com.codemonks.ludo_engine.service.BotTurnService;
import com.codemonks.ludo_engine.service.SupabaseRealtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotTurnServiceImpl implements BotTurnService {

    private final BotMoveService botMoveService;
    private final SupabaseRealtimeService realtimeService;
    private final ObjectMapper objectMapper;

    @Override
    public void triggerBotIfNeeded(
            Long roomId,
            String roomCode
    ) {

        RealtimeGameStateDTO realtimeState =
                realtimeService.getGameState(roomId);

        if (realtimeState == null ||
                realtimeState.getGameState() == null) {

            log.warn(
                    "[BOT_TRIGGER_SKIPPED] No game state found. Room:{}",
                    roomId
            );

            return;
        }

        Map<String, Object> raw = realtimeState.getGameState();

        Object board = raw.get("board");

        Map<String, Object> state = board instanceof Map
                        ? (Map<String, Object>) board
                        : raw;

        GameStateDTO gameState = objectMapper.convertValue(state, GameStateDTO.class);

        if (gameState.getPlayers() == null ||
                gameState.getCurrentTurnPlayerId() == null) {

            log.warn("[BOT_TRIGGER_SKIPPED] Invalid game state. Room:{}", roomId);

            return;
        }
        PlayerDTO currentPlayer = gameState.getPlayers()
                        .stream()
                        .filter(player ->
                                gameState.getCurrentTurnPlayerId().equals(
                                        player.getPlayerId()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (currentPlayer == null) {
            log.warn(
                    "[BOT_TRIGGER_SKIPPED] Current player not found. Room:{} Player:{}",
                    roomId, gameState.getCurrentTurnPlayerId()
            );
            return;
        }

        if (!Boolean.TRUE.equals(currentPlayer.getIsBot())) {

            log.debug(
                    "[BOT_NOT_REQUIRED] Room:{} Player:{} is human",
                    roomId, currentPlayer.getPlayerId());
            return;
        }

        String effectiveRoomCode = roomCode != null
                        ? roomCode
                        : realtimeState.getRoomCode();

        log.info(
                "[BOT_TURN_TRIGGERED] Room:{} Player:{}",
                roomId, currentPlayer.getPlayerId());
        botMoveService.processBotTurn(
                gameState,
                roomId,
                effectiveRoomCode,
                currentPlayer.getPlayerId()
        );
    }
}