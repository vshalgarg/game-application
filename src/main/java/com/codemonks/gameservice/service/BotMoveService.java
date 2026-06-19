package com.codemonks.gameservice.service;

import com.codemonks.gameservice.constants.BotConstants;
import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.enums.RoomRealtimeStatusEnum;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.enums.BotDifficultyEnum;
import com.codemonks.gameservice.engineModule.enums.GameStatusEnum;
import com.codemonks.gameservice.engineModule.factory.GameEngineFactory;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.mapper.GameMapper;
import com.codemonks.gameservice.mapper.LobbyMapper;
import com.codemonks.gameservice.repository.GameResultEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.codemonks.gameservice.constants.BotConstants.BOT_USER_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotMoveService {

    private static final int BOT_DELAY_MS = 1000;

    private final GameEngineFactory gameEngineFactory;
    private final SupabaseService supabaseService;
    private final RoomEntityRepository roomRepository;
    private final GameResultEntityRepository gameResultEntityRepository;

    @Async("botMoveExecutor")
    public void processBotMove(
            RoomEntity room,
            List<PlayerEntity> roomPlayers,
            List<PlayerDto> playerDtos,
            Map<String, Object> gameStateAfterHumanMove,
            long versionAfterHumanMove,
            BotDifficultyEnum botDifficulty
    ) {
        try {
            log.info("[BOT_MOVE] Starting bot move delay. roomId={}", room.getId());

            // 1. Delay — bot "thinking"
            Thread.sleep(BOT_DELAY_MS);

            // 2. Build bot move request
            EngineMoveRequestDTO botRequest = EngineMoveRequestDTO.builder()
                    .roomId(room.getId())
                    .gameState(gameStateAfterHumanMove)
                    .currentTurnUserId(BotConstants.BOT_USER_ID)
                    .userId(BotConstants.BOT_USER_ID)
                    .moveData(null) // engine picks move internally via strategy
                    .players(playerDtos)
                    .botDifficulty(botDifficulty)
                    .build();

            // 3. Call engine for bot move
            GameEngine engine = gameEngineFactory.getStrategy(room.getGameType());
            EngineGameStateResponseDTO botState = engine.processMove(botRequest);

            // 4. Publish bot move state to Supabase
            RealtimeGameStateDTO botRealtimeState = GameMapper.toRealtimeState(
                    room, botState, versionAfterHumanMove + 1
            );
            supabaseService.upsertGameState(botRealtimeState);
            log.info("[BOT_MOVE] Bot move published to Supabase. roomId={}", room.getId());

            // 5. Handle game over if bot won or draw
            if (GameStatusEnum.WIN.equals(botState.getStatus())
                    || GameStatusEnum.DRAW.equals(botState.getStatus())) {

                room.setStatus(RoomStatusEnum.COMPLETED);
                room.setEndedAt(LocalDateTime.now());
                roomRepository.save(room);

                RealtimeLobbyDTO lobbyDTO = LobbyMapper.toLobbyDTO(
                        room, roomPlayers, RoomRealtimeStatusEnum.COMPLETED
                );
                supabaseService.upsertLobbyState(lobbyDTO);

                saveGameResult(room, botState);
                log.info("[BOT_MOVE] Game over after bot move. roomId={}, status={}",
                        room.getId(), botState.getStatus());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[BOT_MOVE] Bot move thread interrupted. roomId={}", room.getId());
        } catch (Exception e) {
            log.error("[BOT_MOVE] Error during bot move. roomId={}, error={}",
                    room.getId(), e.getMessage(), e);
        }
    }

    private void saveGameResult(RoomEntity room, EngineGameStateResponseDTO state) {
        com.codemonks.gameservice.entity.GameResultEntity result =
                com.codemonks.gameservice.entity.GameResultEntity.builder()
                        .tenantId(room.getTenantId())
                        .room(room)
                        .winnerId(state.getWinnerUserId())
                        .completedAt(LocalDateTime.now())
                        .build();
        gameResultEntityRepository.save(result);
        log.info("[BOT_MOVE] Game result saved. roomId={}, winnerId={}",
                room.getId(), state.getWinnerUserId());
    }
}
