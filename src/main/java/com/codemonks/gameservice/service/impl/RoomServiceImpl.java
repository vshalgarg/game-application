package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.dto.request.JoinRoomRequestDTO;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.entity.GameConfigEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.RoomMapper;
import com.codemonks.gameservice.repository.GameConfigEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.repository.PlayerEntityRepository;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.*;
import static com.codemonks.gameservice.enums.RoomStatusEnum.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomEntityRepository roomRepository;
    private final PlayerEntityRepository PlayerEntityRepository;
    private final GameConfigEntityRepository gameConfigRepository;
    private final GameService gameService;

    @Transactional
    @Override
    public RoomResponseDTO createRoom(CreateRoomRequestDTO request) {

        log.info("Creating room for tenantId={}, userId={}", request.getTenantId(), request.getUserId());

        String roomCode = generateRoomCode();

        RoomEntity room = RoomMapper.toRoomEntity(request, roomCode);
        room = roomRepository.save(room);

        log.debug("Room created with id={} and code={}", room.getId(), room.getRoomCode());

        PlayerEntity player = RoomMapper.toHostPlayer(request, room);
        PlayerEntityRepository.save(player);

        log.info(
                "Room created successfully. roomId={}, roomCode={}, hostUserId={}",
                room.getId(),
                room.getRoomCode(),
                request.getUserId()
        );
        return RoomMapper.toRoomResponse(room);
    }

    @Transactional
    @Override
    public RoomResponseDTO joinRoom(String roomCode, JoinRoomRequestDTO request) {

        log.info("User joining room. roomCode={}, userId={}", roomCode, request.getUserId());
        RoomEntity room = roomRepository.findByRoomCodeAndTenantId(roomCode, request.getTenantId())
                .orElseThrow(() -> {
                    log.error("Room not found for code={}", roomCode);
                    return new ResourceNotFoundException(ROOM_NOT_FOUND);
                });

        boolean existingPlayer = PlayerEntityRepository
                .existsByRoom_IdAndUserId(
                        room.getId(),
                        request.getUserId()
                );

        if (room.getStatus() == ACTIVE) {
            if (!existingPlayer) {
                log.warn("Join rejected. Game already started. roomId={}, userId={}",
                        room.getId(),
                        request.getUserId());
                throw new GameException(GAME_ALREADY_STARTED);
            }
            log.info("Player reconnected to in-game room. roomId={}, userId={}",
                    room.getId(),
                    request.getUserId());
            return RoomMapper.toRoomResponse(room);
        }

        if (existingPlayer) {
            log.warn("User already in room. roomId={}, userId={}",
                    room.getId(),
                    request.getUserId());
            throw new GameException(USER_ALREADY_IN_ROOM);
        }

        GameConfigEntity config = gameConfigRepository
                .findByIdTenantIdAndIdGameType(room.getTenantId(), room.getGameType())
                .orElseThrow(() -> {
                    log.error("Game config missing for tenantId={}, gameType={}", room.getTenantId(), room.getGameType());
                    return new GameException(GAME_CONFIG_NOT_FOUND);
                });

        int currentPlayers = PlayerEntityRepository.countByRoom_Id(room.getId());

        if (currentPlayers >= config.getMaxPlayers()) {
            log.warn("Room full. roomId={}, maxPlayers={}", room.getId(), config.getMaxPlayers());
            throw new GameException(ROOM_FULL);
        }

        PlayerEntity player = RoomMapper.toJoinPlayer(request, room);
        PlayerEntityRepository.save(player);

        log.info("User joined room. roomId={}, userId={}", room.getId(), request.getUserId());

//        if (currentPlayers + 1 == config.getMaxPlayers()) {
//            room.setStatus(RoomStatusEnum.FULL);
//            roomRepository.save(room);
//            log.info(
//                    "Room reached maximum capacity. roomId={}",
//                    room.getId()
//            );
//        }

        log.info(
                "User joined room successfully. roomId={}, roomCode={}, userId={}",
                room.getId(),
                roomCode,
                request.getUserId()
        );
        return RoomMapper.toRoomResponse(room);
    }

    @Transactional
    @Override
    public EngineGameStateResponseDTO startGame(String roomCode, Long userId) {

        log.info("Start game request. roomCode={}, userId={}", roomCode, userId);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        // Check if user is host
        PlayerEntity player = PlayerEntityRepository
                .findByRoom_IdAndUserId(room.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (player.getRole() != RoomPlayerRole.HOST) {
            throw new GameException(ResponseErrorCodes.ONLY_HOST_CAN_START_GAME);
        }

        room.setStatus(ACTIVE);
        roomRepository.save(room);
        log.info("Game started. roomId={}", room.getId());
       return gameService.startGame(roomCode);
    }

    @Transactional(readOnly = true)
    @Override
    public RoomDetailsResponseDTO getRoomDetails(String roomCode) {

        log.info("Fetching room details for roomCode={}", roomCode);

        RoomEntity room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        List<PlayerEntity> players =
                PlayerEntityRepository.findByRoom_Id(room.getId());

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