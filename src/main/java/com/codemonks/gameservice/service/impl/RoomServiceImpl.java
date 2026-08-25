package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.dto.ResponseMessages;
import com.codemonks.gameservice.dto.request.*;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.enums.RoomRealtimeStatusEnum;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.factory.GameEngineFactory;
import com.codemonks.gameservice.engineModule.model.BoardLayout;
import com.codemonks.gameservice.entity.GameConfigEntity;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.LobbyMapper;
import com.codemonks.gameservice.mapper.RoomMapper;
import com.codemonks.gameservice.repository.GameConfigEntityRepository;
import com.codemonks.gameservice.repository.PlayerEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.service.BotService;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.RoomService;
import com.codemonks.gameservice.service.gameroom.factory.GameRoomStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final GameEngineFactory gameEngineFactory;
    private final BotService botService;
    private final GameRoomStrategyFactory gameRoomStrategyFactory;

    @Transactional
    @Override
    public RoomResponseDTO createRoom(CreateRoomRequestDTO request) {
        log.info("Creating room. tenantId={}, userId={}",
                request.getTenantId(), request.getUserId());

        gameRoomStrategyFactory.getStrategy(request.getGameType())
                .validateCreateRequest(request);

        String roomCode = generateRoomCode();
        RoomEntity room = roomRepository.save(RoomMapper.toRoomEntity(request, roomCode));
        PlayerEntity host = playerRepository.save(RoomMapper.toHostPlayer(request, room));
        RealtimeLobbyDTO lobbyDTO = LobbyMapper.toLobbyDTO(room, List.of(host), RoomRealtimeStatusEnum.WAITING);

        gameEngineFactory.getStrategy(room.getGameType()).publishLobbyState(lobbyDTO);

        log.info("Room created. roomId={}, roomCode={}, hostUserId={}",
                room.getId(), room.getRoomCode(), request.getUserId());
        return RoomMapper.toRoomResponse(room, host);
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
                .orElseThrow(() -> new GameException(GAME_CONFIG_NOT_FOUND));

        List<PlayerEntity> players = playerRepository.findByRoom_Id(room.getId());

        if (players.size() >= config.getMaxPlayers()) {
            throw new GameException(ROOM_FULL);
        }

// ── Duplicate-join check
        boolean alreadyInRoom = playerRepository.existsByRoom_IdAndUserId(room.getId(), request.getUserId());
        if (alreadyInRoom) {
            log.warn("User already in room. roomId={}, userId={}", room.getId(), request.getUserId());
            throw new GameException(USER_ALREADY_IN_ROOM);
        }
        PlayerEntity player = playerRepository.save(RoomMapper.toJoinPlayer(request, room));

        players = playerRepository.findByRoom_Id(room.getId());

        RoomRealtimeStatusEnum status =
                players.size() >= config.getMaxPlayers()
                        ? RoomRealtimeStatusEnum.READY
                        : RoomRealtimeStatusEnum.WAITING;

        RealtimeLobbyDTO lobbyDTO = LobbyMapper.toLobbyDTO(
                        room,
                        players,
                        status
                );

        gameEngineFactory.getStrategy(room.getGameType()).publishLobbyState(lobbyDTO);

        log.info("User joined. roomId={}, userId={}, lobbyStatus={}",
                room.getId(), request.getUserId(), status);

        return RoomMapper.toRoomResponse(room, player);
    }

    @Transactional
    @Override
    public RoomActionResponseDTO addBot(String roomCode, AddBotRequestDTO request) {
        log.info(
                "Add bot request. roomCode={}, hostUserId={}",
                roomCode, request.getHostUserId());

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));


        gameRoomStrategyFactory.getStrategy(room.getGameType())
                .validateAddBotRequest(request);

        PlayerEntity host = playerRepository.findByRoom_IdAndUserId(
                        room.getId(), request.getHostUserId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (host.getRole() != RoomPlayerRole.HOST) {
            throw new GameException(ONLY_HOST_CAN_START_GAME);
        }

        if (room.getStatus() != RoomStatusEnum.WAITING) {
            throw new GameException(GAME_ALREADY_STARTED);
        }

        GameConfigEntity config = gameConfigRepository.findByIdTenantIdAndIdGameType(
                        room.getTenantId(),
                        room.getGameType())
                .orElseThrow(() -> {
                    log.error(
                            "Config missing. tenantId={}, gameType={}",
                            room.getTenantId(),
                            room.getGameType());
                    return new GameException(GAME_CONFIG_NOT_FOUND);});
        List<PlayerEntity> players = playerRepository.findByRoom_Id(room.getId());
        if (players.size() >= config.getMaxPlayers()) {
            throw new GameException(ROOM_FULL);
        }

        if (room.getBotDifficulty() == null
                && request.getBotDifficulty() == null) {
            throw new GameException(INVALID_REQUEST);
        }

        Long botUserId = botService.getNextBotUserId(players);

        PlayerEntity bot = RoomMapper.toBotPlayer(room.getTenantId(), room, botUserId);
        playerRepository.save(bot);


        if (room.getBotDifficulty() == null) {
            room.setBotDifficulty(request.getBotDifficulty());
            room.setMatchType(com.codemonks.gameservice.engineModule.enums.MatchTypeEnum.BOT);
            roomRepository.save(room);

            log.info(
                    "Bot difficulty initialized. roomId={}, difficulty={}, matchType={}",
                    room.getId(),
                    room.getBotDifficulty(),
                    room.getMatchType()
            );
        }

        log.info(
                "Bot added. roomId={}, botUserId={}, botDifficulty={}",
                room.getId(),
                botUserId,
                room.getBotDifficulty()
        );

        players = playerRepository.findByRoom_Id(room.getId());

        RoomRealtimeStatusEnum status =
                players.size() >= config.getMaxPlayers()
                        ? RoomRealtimeStatusEnum.READY
                        : RoomRealtimeStatusEnum.WAITING;

        RealtimeLobbyDTO lobbyDTO = LobbyMapper.toLobbyDTO(room, players, status);
        gameEngineFactory.getStrategy(room.getGameType())
                .publishLobbyState(lobbyDTO);

        RoomDetailsResponseDTO roomDetails = RoomMapper.toRoomDetailsResponseDTO(room, players);

        return RoomActionResponseDTO.builder()
                .roomDetails(roomDetails)
                .message(ResponseMessages.BOT_ADDED)
                .build();
    }

    @Override
    @Transactional
    public RoomActionResponseDTO removePlayer(String roomCode, RemovePlayerRequestDTO request) {

        log.info(
                "Remove participant request. roomCode={}, hostUserId={}, userId={}",
                roomCode,
                request.getHostUserId(),
                request.getUserId()
        );

        RoomEntity room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ROOM_NOT_FOUND));
        PlayerEntity host = playerRepository
                .findByRoom_IdAndUserId(
                        room.getId(),
                        request.getHostUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(USER_NOT_FOUND));
        if (host.getRole() != RoomPlayerRole.HOST) {
            throw new GameException(ONLY_HOST_CAN_START_GAME);
        }

        if (room.getStatus() != RoomStatusEnum.WAITING) {
            throw new GameException(GAME_ALREADY_STARTED);
        }

        if (request.getHostUserId().equals(request.getUserId())) {
            throw new GameException(HOST_CANNOT_REMOVE_SELF);
        }

        PlayerEntity participant = playerRepository
                .findByRoom_IdAndUserId(
                        room.getId(),
                        request.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(USER_NOT_FOUND));
        List<PlayerEntity> players =
                playerRepository.findByRoom_Id(room.getId());

        playerRepository.delete(participant);

        players = playerRepository.findByRoom_Id(room.getId());
        boolean botExists = players.stream()
                .anyMatch(player -> player.getRole() == RoomPlayerRole.BOT);

        if (!botExists) {
            room.setBotDifficulty(null);
            roomRepository.save(room);
        }

        log.info("Bot difficulty cleared. roomId={}", room.getId());

        GameConfigEntity config = gameConfigRepository
                .findByIdTenantIdAndIdGameType(
                        room.getTenantId(),
                        room.getGameType()
                )
                .orElseThrow(() -> new GameException(GAME_CONFIG_NOT_FOUND));
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

        gameEngineFactory
                .getStrategy(room.getGameType())
                .publishLobbyState(lobbyDTO);

        RoomDetailsResponseDTO roomDetails =
                RoomMapper.toRoomDetailsResponseDTO(
                        room,
                        players
                );
        String message =
                participant.getRole() == RoomPlayerRole.BOT
                        ? ResponseMessages.BOT_REMOVED
                        : ResponseMessages.PLAYER_REMOVED;
        log.info(
                "Remove participant completed. roomId={}, remainingPlayers={}",
                room.getId(),
                players.size()
        );

        return RoomActionResponseDTO.builder()
                .roomDetails(roomDetails)
                .message(message)
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public BoardLayout getBoardLayout(String roomCode) {

        log.info("Fetching board layout for roomCode={}", roomCode);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        log.info("Board layout requested. roomCode={}, gameType={}",
                roomCode, room.getGameType());

        return gameEngineFactory.getStrategy(room.getGameType()).getBoardLayout();
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
            throw new GameException(GAME_ALREADY_STARTED);}

        List<PlayerEntity> players = playerRepository.findByRoom_Id(room.getId());
        if (players.size() < 2) {
            log.warn("Cannot start game — insufficient players. roomId={}, playerCount={}",
                    room.getId(), players.size());
            throw new GameException(MINIMUM_PLAYERS_REQUIRED);
        }

        if (room.getMatchType() == null) {
            room.setMatchType(com.codemonks.gameservice.engineModule.enums.MatchTypeEnum.PVP);
            log.info("MatchType finalized as PVP. roomId={}", room.getId());
        }
        room.setStatus(ACTIVE);
        roomRepository.save(room);

        log.info("Game starting. roomId={}, matchType={}", room.getId(), room.getMatchType());
        return gameService.startGame(room);
    }

    @Transactional(readOnly = true)
    @Override
    public RoomDetailsResponseDTO getRoomDetails(String roomCode) {

        log.info("Fetching room details for roomCode={}", roomCode);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        List<PlayerEntity> players = playerRepository.findByRoom_Id(room.getId());

        log.info(
                "Room details fetched successfully for roomCode={}, totalPlayers={}",
                roomCode, players.size());

        return RoomMapper.toRoomDetailsResponseDTO(room, players);
    }

    @Transactional
    @Override
    public EngineGameStateResponseDTO restartGame(String roomCode, Long userId) {
        log.info("Restart game request. roomCode={}, userId={}", roomCode, userId);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        PlayerEntity player = playerRepository.findByRoom_IdAndUserId(room.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (player.getRole() != RoomPlayerRole.HOST) {
            throw new GameException(ONLY_HOST_CAN_START_GAME);
        }

        room.setStatus(RoomStatusEnum.ACTIVE);
        room.setStartedAt(LocalDateTime.now());
        room.setEndedAt(null);
        roomRepository.save(room);

        log.info("Game restarting. roomId={}, matchType={}", room.getId(),
                room.getMatchType());
        return gameService.startGame(room);
    }
    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}