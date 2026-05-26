package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.dto.request.JoinRoomRequestDTO;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.enums.RoomRealtimeStatusEnum;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.entity.GameConfigEntity;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.LobbyMapper;
import com.codemonks.gameservice.mapper.RoomMapper;
import com.codemonks.gameservice.repository.GameConfigEntityRepository;
import com.codemonks.gameservice.repository.PlayerEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.RoomService;
import com.codemonks.gameservice.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.*;
import static com.codemonks.gameservice.enums.RoomStatusEnum.ACTIVE;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomEntityRepository roomRepository;
    private final PlayerEntityRepository playerRepository;
    private final GameConfigEntityRepository gameConfigRepository;
    private final GameService gameService;
    private final SupabaseService supabaseService;

    @Transactional
    @Override
    public RoomResponseDTO createRoom(CreateRoomRequestDTO request) {
        log.info("Creating room. tenantId={}, userId={}",
                request.getTenantId(), request.getUserId());

        String roomCode = generateRoomCode();
        RoomEntity room = roomRepository.save(RoomMapper.toRoomEntity(request, roomCode));
        PlayerEntity host = playerRepository.save(RoomMapper.toHostPlayer(request, room));
        RealtimeLobbyDTO lobbyDTO =
                LobbyMapper.toLobbyDTO(
                        room,
                        List.of(host),
                        RoomRealtimeStatusEnum.WAITING
                );
        supabaseService.upsertLobbyState(lobbyDTO);

        log.info("Room created. roomId={}, roomCode={}, hostUserId={}",
                room.getId(), room.getRoomCode(), request.getUserId());

        return RoomMapper.toRoomResponse(
                room,
                RoomPlayerRole.HOST
        );
    }

    @Transactional
    @Override
    public RoomResponseDTO joinRoom(String roomCode, JoinRoomRequestDTO request) {
        log.info("User joining room. roomCode={}, userId={}",
                roomCode, request.getUserId());

        RoomEntity room = roomRepository
                .findByRoomCodeAndTenantId(roomCode, request.getTenantId())
                .orElseThrow(() -> {
                    log.error("Room not found. roomCode={}", roomCode);
                    return new ResourceNotFoundException(ROOM_NOT_FOUND);
                });


        GameConfigEntity config = gameConfigRepository
                .findByIdTenantIdAndIdGameType(room.getTenantId(), room.getGameType())
                .orElseThrow(() -> {
                    log.error("Config missing. tenantId={}, gameType={}",
                            room.getTenantId(), room.getGameType());
                    return new GameException(GAME_CONFIG_NOT_FOUND);
                });

        int currentPlayers = playerRepository.countByRoom_Id(room.getId());
        playerRepository.save(RoomMapper.toJoinPlayer(request, room));

        List<PlayerEntity> players = playerRepository.findByRoom_Id(room.getId());

        RoomRealtimeStatusEnum status =
                players.size() >= config.getMaxPlayers()
                        ? RoomRealtimeStatusEnum.READY
                        : RoomRealtimeStatusEnum.WAITING;

        RealtimeLobbyDTO lobbyDTO =
                LobbyMapper.toLobbyDTO(
                        room,
                        players,
                        status
                );
        supabaseService.upsertLobbyState(lobbyDTO);

        log.info("User joined. roomId={}, userId={}, lobbyStatus={}",
                room.getId(), request.getUserId(), status);

        return RoomMapper.toRoomResponse(room, RoomPlayerRole.PLAYER);
    }

    @Transactional
    @Override
    public EngineGameStateResponseDTO startGame(String roomCode, Long userId) {
        log.info("Start game request. roomCode={}, userId={}", roomCode, userId);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        PlayerEntity player = playerRepository
                .findByRoom_IdAndUserId(room.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (player.getRole() != RoomPlayerRole.HOST) {
            throw new GameException(ONLY_HOST_CAN_START_GAME);
        }

        if (room.getStatus() == ACTIVE) {
            throw new GameException(GAME_ALREADY_STARTED);
        }
        room.setStatus(ACTIVE);
        roomRepository.save(room);

        log.info("Game starting. roomId={}", room.getId());
        return gameService.startGame(room);
    }

    @Transactional(readOnly = true)
    @Override
    public RoomDetailsResponseDTO getRoomDetails(String roomCode) {

        log.info("Fetching room details for roomCode={}", roomCode);

        RoomEntity room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        List<PlayerEntity> players = playerRepository.findByRoom_Id(room.getId());

        log.info(
                "Room details fetched successfully for roomCode={}, totalPlayers={}",
                roomCode,
                players.size()
        );

        return RoomMapper.toRoomDetailsResponseDTO(room, players);
    }

    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}