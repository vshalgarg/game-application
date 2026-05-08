package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeMoveDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDto;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.factory.GameEngineFactory;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.entity.GameEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.RoomPlayerEntity;
import com.codemonks.gameservice.enums.GameStatusEnum;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.GameMapper;
import com.codemonks.gameservice.mapper.PlayerMapper;
import com.codemonks.gameservice.repository.GameEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.repository.RoomPlayerEntityRepository;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.GAME_NOT_FOUND;
import static com.codemonks.gameservice.constants.ResponseErrorCodes.ROOM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {

    private final RoomEntityRepository roomRepository;
    private final RoomPlayerEntityRepository roomPlayerRepository;
    private final GameEngineFactory gameEngineFactory;
    private final GameEntityRepository gameRepository;
    private final SupabaseService supabaseService;

    @Override
    public void startGame(String roomCode) {

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ROOM_NOT_FOUND));
        List<RoomPlayerEntity> players =
                roomPlayerRepository.findByRoomCode(roomCode);

        // build engine req
        EngineStartGameRequestDto request =
                GameMapper.toStartGameRequest(room, players);

        GameEngine strategy =
                gameEngineFactory.getStrategy(room.getGameType());

        EngineGameStateResponseDTO engineResponse = strategy.startGame(request);
        // Persist game data
        GameEntity game =
                saveGameState(room, engineResponse);

        // build realtime dto
        RealtimeGameStateDTO realtimeState =
                GameMapper.toRealtimeState(
                        game,
                        roomCode,
                        engineResponse
                );

        // Publish realtime event
        publishGameStartedEvent(realtimeState);
        log.info("Game engine triggered successfully. roomCode={}",
                roomCode);
    }

    @Override
    public EngineGameStateResponseDTO makeMove(
            String roomCode,
            MakeMoveRequestDTO makeMoveRequestDTO
    ) {
        log.info("Move request received. roomCode={}, userId={}",
                roomCode,
                makeMoveRequestDTO.getUserId());

        // Validate room exists
        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ROOM_NOT_FOUND));

        List<RoomPlayerEntity> roomPlayers =
                roomPlayerRepository.findByRoomCode(roomCode);

        // Fetch active game
        GameEntity game = gameRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                GAME_NOT_FOUND
                        ));

        // Fetch realtime game state from Supabase
        RealtimeGameStateDTO currentState =
                supabaseService.getGameState(
                        game.getId().toString()
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
                        .gameId(game.getId())
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

        // Build move dto
        RealtimeMoveDTO moveDTO =
                RealtimeMoveDTO.builder()
                        .gameId(game.getId())
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

        RealtimeGameStateDTO realtimeState =
                GameMapper.toRealtimeState(
                        game,
                        roomCode,
                        updatedState
                );

        // Build updated realtime state
        supabaseService.updateGameState(realtimeState);


        // If game completed save result
        if(updatedState.getStatus() ==
                GameStatusEnum.FINISHED) {
            game.setStatus(GameStatusEnum.FINISHED);
            gameRepository.save(game);
            // save winner/result here
        }
        log.info("Move processed successfully. gameId={}",
                game.getId());

        // Return updated state
        return updatedState;
    }

    private GameEntity saveGameState(
            RoomEntity room,
            EngineGameStateResponseDTO response
    ) {

        GameEntity game = GameEntity.builder()
                .roomCode(room.getRoomCode())
                .tenantId(room.getTenantId())
                .status(response.getStatus())
                .currentTurn(response.getCurrentTurnUserId())
                .build();

        return gameRepository.save(game);
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
}
