package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeMoveDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDto;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.enums.GameStatusEnum;
import com.codemonks.gameservice.engineModule.factory.GameEngineFactory;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.entity.GameResultEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.GameMapper;
import com.codemonks.gameservice.mapper.PlayerMapper;
import com.codemonks.gameservice.repository.GameResultEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.repository.PlayerEntityRepository;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {

    private final RoomEntityRepository roomRepository;
    private final PlayerEntityRepository PlayerEntityRepository;
    private final GameEngineFactory gameEngineFactory;
    private final SupabaseService supabaseService;
    private final GameResultEntityRepository gameResultEntityRepository;

    @Override
    public EngineGameStateResponseDTO startGame(String roomCode) {

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> {
                    log.error("Room not found. roomCode={}", roomCode);
                    return new ResourceNotFoundException(ROOM_NOT_FOUND);
                        });
        List<PlayerEntity> players =
                PlayerEntityRepository.findByRoom_Id(room.getId());

        // build engine req
        EngineStartGameRequestDto request =
                GameMapper.toStartGameRequest(room, players);

        GameEngine strategy =
                gameEngineFactory.getStrategy(room.getGameType());

        EngineGameStateResponseDTO engineResponse = strategy.startGame(request);
        log.info(
                "Game engine processed successfully. roomCode={}",
                roomCode
        );

        // build realtime dto
        RealtimeGameStateDTO realtimeState =
                GameMapper.toRealtimeState(
                        room,
                        engineResponse
                );

        // Publish realtime event
        publishGameStartedEvent(realtimeState);
        log.info(
                "Realtime game state published successfully. roomCode={}, roomId={}",
                room.getRoomCode(),
                room.getId()
        );
        return engineResponse;
    }

    @Transactional
    @Override
    public EngineGameStateResponseDTO makeMove(
            String roomCode,
            MakeMoveRequestDTO makeMoveRequestDTO
    ) {

        // Validate room exists
        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                {
                    log.error("room not found. roomCode={}", roomCode);
                   return new ResourceNotFoundException(ROOM_NOT_FOUND);
                });


        List<PlayerEntity> roomPlayers =
                PlayerEntityRepository.findByRoom_Id(room.getId());

        // Fetch realtime game state from Supabase
        RealtimeGameStateDTO currentState =
                supabaseService.getGameState(
                        room.getId().toString()
                );

        log.info(
                "Realtime game state fetched successfully. roomId={}",
                room.getId()
        );

        // Resolve engine
        GameEngine strategy =
                gameEngineFactory.getStrategy(
                        room.getGameType()
                );

        // Build engine request
        Map<String, Object> moveData = new HashMap<>();

        moveData.put("row", makeMoveRequestDTO.getRow());
        moveData.put("col", makeMoveRequestDTO.getCol());

       List<PlayerDto> players = PlayerMapper.toPlayerDtos(roomPlayers);

        EngineMoveRequestDTO engineRequest =
                EngineMoveRequestDTO.builder()
                        .roomId(room.getId())
                        .boardState(currentState.getBoardState())
                        .currentTurnUserId(
                                currentState.getCurrentTurnUserId()
                        )
                        .userId(makeMoveRequestDTO.getUserId())
                        .moveData(moveData)
                        .players(players)
                        .build();

        // Engine validates move + computes next state
        EngineGameStateResponseDTO updatedState =
                strategy.processMove(engineRequest);
        log.info(
                "Game engine processed move successfully. roomId={}, userId={}",
                room.getId(),
                makeMoveRequestDTO.getUserId()
        );

        // Build move dto
        RealtimeMoveDTO moveDTO =
                RealtimeMoveDTO.builder()
                        .roomId(room.getId())
                        .moveNumber(
                                calculateMoveNumber(
                                        currentState.getBoardState()
                                )
                        )
                        .playerId(
                                makeMoveRequestDTO.getUserId()
                        )
                        .moveData(moveData)
                        .build();

        // Save move history
        supabaseService.saveMove(moveDTO);
        log.info(
                "Realtime move saved successfully. roomId={}, userId={}",
                room.getId(),
                makeMoveRequestDTO.getUserId()
        );

        RealtimeGameStateDTO realtimeState =
                GameMapper.toRealtimeState(
                        room,
                        updatedState
                );

        // Build updated realtime state
        supabaseService.updateGameState(realtimeState);
        log.info(
                "Realtime game state updated successfully. roomId={}",
                room.getId()
        );


        // If game completed save result
        if (GameStatusEnum.WIN.equals(updatedState.getStatus())
                || GameStatusEnum.DRAW.equals(updatedState.getStatus())) {

            room.setStatus(RoomStatusEnum.COMPLETED);
            roomRepository.save(room);
            saveGameResult(room, updatedState);
            log.info(
                    "Game completed successfully. roomId={}, winnerUserId={}",
                    room.getId(),
                    updatedState.getWinnerUserId()
            );
        }

        log.info(
                "Move processed successfully. roomId={}",
                room.getId()
        );

        return updatedState;
    }

    private void publishGameStartedEvent(
            RealtimeGameStateDTO realtimeGameStateDTO
    ) {
        // supabase realtime
        supabaseService.createInitialState(realtimeGameStateDTO);
    }

    private int calculateMoveNumber(
            List<List<String>> board
    ) {

        return (int) board.stream()
                .flatMap(List::stream)
                .filter(cell ->
                        cell != null &&
                                !cell.isBlank()
                )
                .count() + 1;
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
