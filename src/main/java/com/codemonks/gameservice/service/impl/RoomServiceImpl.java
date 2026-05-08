package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.dto.request.JoinRoomRequestDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.entity.GameConfigEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.RoomPlayerEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.RoomMapper;
import com.codemonks.gameservice.repository.GameConfigEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.repository.RoomPlayerEntityRepository;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.*;
import static com.codemonks.gameservice.enums.RoomStatusEnum.IN_GAME;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomEntityRepository roomRepository;
    private final RoomPlayerEntityRepository roomPlayerRepository;
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

        RoomPlayerEntity player = RoomMapper.toHostPlayer(request, roomCode);
        roomPlayerRepository.save(player);

        log.info("Host added to room. roomId={}, userId={}", room.getId(), request.getUserId());

        return RoomMapper.toRoomResponse(room);
    }

    @Transactional
    @Override
    public RoomResponseDTO joinRoom(String roomCode, JoinRoomRequestDTO request) {

        log.info("User joining room. roomCode={}, userId={}", roomCode, request.getUserId());
        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> {
                    log.error("Room not found for code={}", roomCode);
                    return new ResourceNotFoundException(ROOM_NOT_FOUND);
                });

        boolean existingPlayer = roomPlayerRepository
                .existsByRoomCodeAndUserId(
                        roomCode,
                        request.getUserId()
                );

        if (room.getStatus() == IN_GAME) {
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

        int currentPlayers = roomPlayerRepository.countByRoomCode(roomCode);

        if (currentPlayers >= config.getMaxPlayers()) {
            log.warn("Room full. roomId={}, maxPlayers={}", room.getId(), config.getMaxPlayers());
            throw new GameException(ROOM_FULL);
        }

        RoomPlayerEntity player = RoomMapper.toJoinPlayer(request, roomCode);
        roomPlayerRepository.save(player);

        log.info("User joined room. roomId={}, userId={}", room.getId(), request.getUserId());

        if (currentPlayers + 1 == config.getMaxPlayers()) {
            room.setStatus(RoomStatusEnum.FULL);
            roomRepository.save(room);
            log.info("Room is now FULL. roomId={}", room.getId());
        }
        return RoomMapper.toRoomResponse(room);
    }

    @Transactional
    @Override
    public void startGame(String roomCode, Long userId) {

        log.info("Start game request. roomCode={}, userId={}", roomCode, userId);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        // Check if user is host
        RoomPlayerEntity player = roomPlayerRepository
                .findByRoomCodeAndUserId(roomCode, userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (player.getRole() != RoomPlayerRole.HOST) {
            throw new GameException(ResponseErrorCodes.ONLY_HOST_CAN_START_GAME);
        }

//        if (room.getStatus() != RoomStatusEnum.FULL) {
//            throw new GameException(ROOM_NOT_FULL);
//        }

        room.setStatus(IN_GAME);
        roomRepository.save(room);
        log.info("Game started. roomId={}", room.getId());
        gameService.startGame(roomCode);
    }

    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}