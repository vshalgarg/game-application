package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeMoveDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.enums.RoomRealtimeStatusEnum;
import com.codemonks.gameservice.engineModule.dto.request.DiceRollRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.DiceRollResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.enums.GameStatusEnum;
import com.codemonks.gameservice.engineModule.factory.GameEngineFactory;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.entity.GameResultEntity;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.GameMapper;
import com.codemonks.gameservice.mapper.LobbyMapper;
import com.codemonks.gameservice.mapper.PlayerMapper;
import com.codemonks.gameservice.repository.GameResultEntityRepository;
import com.codemonks.gameservice.repository.PlayerEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.SupabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.GAME_ALREADY_FINISHED;
import static com.codemonks.gameservice.constants.ResponseErrorCodes.ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {

    private final RoomEntityRepository roomRepository;
    private final PlayerEntityRepository playerRepository;
    private final GameEngineFactory gameEngineFactory;
    private final SupabaseService supabaseService;
    private final GameResultEntityRepository gameResultEntityRepository;

    @Override
    @Transactional
    public EngineGameStateResponseDTO startGame(RoomEntity room) {
        List<PlayerEntity> players = playerRepository.findByRoom_Id(room.getId());
        EngineStartGameRequestDTO request = GameMapper.toStartGameRequest(room, players);
        GameEngine engine = gameEngineFactory.getStrategy(room.getGameType());
        EngineGameStateResponseDTO engineResponse = engine.startGame(request);

        RealtimeGameStateDTO realtimeState = GameMapper.toRealtimeState(room, engineResponse, 1L);
        supabaseService.upsertGameState(realtimeState);
        RealtimeLobbyDTO lobbyDTO =
                LobbyMapper.toLobbyDTO(
                        room,
                        players,
                        RoomRealtimeStatusEnum.ACTIVE
                );

        supabaseService.upsertLobbyState(lobbyDTO);

        log.info("Game started and state published. roomCode={}", room.getRoomCode());
        return engineResponse;
    }

    @Transactional
    @Override
    public EngineGameStateResponseDTO makeMove(
            String roomCode,
            MakeMoveRequestDTO makeMoveRequestDTO
    ) {
        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> {
                    log.error("Room not found. roomCode={}", roomCode);
                    return new ResourceNotFoundException(ROOM_NOT_FOUND);
                });

        RealtimeGameStateDTO currentState = supabaseService.getGameState(room.getId());
        GameStatusEnum gameStatus = GameStatusEnum.valueOf(currentState.getGameStatus());
        if (gameStatus == GameStatusEnum.WIN || gameStatus == GameStatusEnum.DRAW) {
            log.error("Move on finished game. roomId={}", room.getId());
            throw new GameException(
                    GAME_ALREADY_FINISHED,
                    "Game ended. Winner: " + currentState.getWinnerUserId()
            );
        }

        List<PlayerEntity> roomPlayers = playerRepository.findByRoom_Id(room.getId());
        List<PlayerDto> players = PlayerMapper.toPlayerDtos(roomPlayers);
        EngineMoveRequestDTO moveRequest = EngineMoveRequestDTO.builder()
                .roomId(room.getId())
                .gameState(currentState.getGameState())
                .currentTurnUserId(currentState.getCurrentTurnUserId())
                .userId(makeMoveRequestDTO.getUserId())
                .moveData(makeMoveRequestDTO.getMoveData())
                .players(players)
                .botDifficulty(room.getBotDifficulty())
                .build();
        GameEngine engine = gameEngineFactory.getStrategy(room.getGameType());
        EngineGameStateResponseDTO updatedState = engine.processMove(moveRequest);

        RealtimeMoveDTO moveDTO = RealtimeMoveDTO.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .playerId(makeMoveRequestDTO.getUserId())
                .moveData(makeMoveRequestDTO.getMoveData())
                .build();
        supabaseService.saveMove(moveDTO);

        RealtimeGameStateDTO realtimeState =
                GameMapper.toRealtimeState(
                        room,
                        updatedState,
                        currentState.getVersion() + 1
                );
        supabaseService.upsertGameState(realtimeState);

        if (GameStatusEnum.WIN.equals(updatedState.getStatus())
                || GameStatusEnum.DRAW.equals(updatedState.getStatus())) {
            room.setStatus(RoomStatusEnum.COMPLETED);
            roomRepository.save(room);
            RealtimeLobbyDTO lobbyDTO =
                    LobbyMapper.toLobbyDTO(
                            room,
                            roomPlayers,
                            RoomRealtimeStatusEnum.COMPLETED
                    );

            supabaseService.upsertLobbyState(
                    lobbyDTO
            );
            saveGameResult(room, updatedState);
            log.info("Game completed. roomId={}, winner={}",
                    room.getId(), updatedState.getWinnerUserId());
        }

        log.info("Move processed. roomId={}, userId={}",
                room.getId(), makeMoveRequestDTO.getUserId()    );
        return updatedState;
    }

    @Override
    public DiceRollResponseDTO rollDice(String roomCode, Long playerId) {

        // 1. Fetch room
        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        // 2. Read authoritative gameState from Supabase — not from client
        RealtimeGameStateDTO currentState = supabaseService.getGameState(room.getId());

        // 3. Guard: game must be running
        GameStatusEnum gameStatus = GameStatusEnum.valueOf(currentState.getGameStatus());
        if (gameStatus == GameStatusEnum.WIN || gameStatus == GameStatusEnum.DRAW) {
            throw new GameException(GAME_ALREADY_FINISHED);
        }

        // 4. Build engine request with server-side state
        DiceRollRequestDTO engineRequest = DiceRollRequestDTO.builder()
                .roomId(room.getId())
                .playerId(playerId)
                .gameState(currentState.getGameState()) // ← already Map<String,Object> from Supabase
                .build();

        // 5. Call engine via factory — generic, works for any game
        GameEngine engine = gameEngineFactory.getStrategy(room.getGameType());
        DiceRollResponseDTO result = engine.rollDice(engineRequest);

        // 6. Write updated state back to Supabase
        // pendingDice and playerTurnStage are now inside result.getGameState()
        RealtimeGameStateDTO updatedRealtimeState = GameMapper.toRealtimeStateFromDiceRoll(
                room, result, currentState.getVersion() + 1
        );
        supabaseService.upsertGameState(updatedRealtimeState);

        // 7. Supabase Realtime broadcasts to all clients automatically
        log.info("Dice rolled. roomId={}, playerId={}, dice={}",
                room.getId(), playerId, result.getDice());

        return result;
    }


    private void saveGameResult(
            RoomEntity room,
            EngineGameStateResponseDTO updatedState
    ) {

        GameResultEntity result = GameResultEntity.builder()
                .tenantId(room.getTenantId())
                .room(room)
                .winnerId(updatedState.getWinnerUserId())
                .completedAt(LocalDateTime.now())
                .build();

        gameResultEntityRepository.save(result);

        log.info(
                "Game result saved successfully. roomId={}, winnerUserId={}",
                room.getId(),
                updatedState.getWinnerUserId()
        );
    }
}
