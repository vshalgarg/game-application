package com.codemonks.gameservice.service;

import com.codemonks.gameservice.dto.request.CreateRoomRequest;
import com.codemonks.gameservice.dto.request.JoinRoomRequest;
import com.codemonks.gameservice.dto.response.RoomResponse;
import com.codemonks.gameservice.entity.GameConfigEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.RoomPlayerEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.mapper.RoomMapper;
import com.codemonks.gameservice.repository.GameConfigEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.repository.RoomPlayerEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomEntityRepository roomRepository;
    private final RoomPlayerEntityRepository roomPlayerRepository;
    private final GameConfigEntityRepository gameConfigRepository;

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {

        log.info("Creating room for tenantId={}, userId={}", request.getTenantId(), request.getUserId());

        String roomCode = generateRoomCode();

        RoomEntity room = RoomMapper.toRoomEntity(request, roomCode);
        room = roomRepository.save(room);

        log.debug("Room created with id={} and code={}", room.getId(), room.getRoomCode());

        RoomPlayerEntity player = RoomMapper.toHostPlayer(request, room.getId());
        roomPlayerRepository.save(player);

        log.info("Host added to room. roomId={}, userId={}", room.getId(), request.getUserId());

        return RoomMapper.toRoomResponse(room);
    }

    @Transactional
    public RoomResponse joinRoom(String roomCode, JoinRoomRequest request) {

        log.info("User joining room. roomCode={}, userId={}", roomCode, request.getUserId());

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> {
                    log.error("Room not found for code={}", roomCode);
                    return new RuntimeException("Room not found");
                });

        if (room.getStatus() == RoomStatusEnum.STARTED) {
            log.warn("Join rejected. Game already started. roomId={}", room.getId());
            throw new RuntimeException("Game already started");
        }

        boolean alreadyJoined = roomPlayerRepository
                .existsByRoomIdAndUserId(room.getId(), request.getUserId());

        if (alreadyJoined) {
            log.warn("User already in room. roomId={}, userId={}", room.getId(), request.getUserId());
            throw new RuntimeException("User already in room");
        }

        GameConfigEntity config = gameConfigRepository
                .findByIdTenantIdAndIdGameType(room.getTenantId(), room.getGameType())
                .orElseThrow(() -> {
                    log.error("Game config missing for tenantId={}, gameType={}", room.getTenantId(), room.getGameType());
                    return new RuntimeException("Game config not found");
                });

        int currentPlayers = roomPlayerRepository.countByRoomId(room.getId());

        if (currentPlayers >= config.getMaxPlayers()) {
            log.warn("Room full. roomId={}, maxPlayers={}", room.getId(), config.getMaxPlayers());
            throw new RuntimeException("Room is full");
        }

        RoomPlayerEntity player = RoomMapper.toJoinPlayer(request, room.getId());
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
    public void startGame(String roomCode, String userId) {

        log.info("Start game request. roomCode={}, userId={}", roomCode, userId);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check if user is host
        RoomPlayerEntity player = roomPlayerRepository
                .findByRoomIdAndUserId(room.getId(), userId)
                .orElseThrow(() -> new RuntimeException("User not in room"));

        if (player.getRole() != RoomPlayerRole.HOST) {
            throw new RuntimeException("Only host can start the game");
        }

        if (room.getStatus() != RoomStatusEnum.FULL) {
            throw new RuntimeException("Room is not full yet");
        }

        room.setStatus(RoomStatusEnum.STARTED);
        roomRepository.save(room);

        log.info("Game started. roomId={}", room.getId());

        // call Game Engine here
    }

    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}