package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.dto.ResponseMessages;
import com.codemonks.gameservice.dto.request.*;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.dto.response.TambolaAvailableRuleResponseDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.enums.RoomRealtimeStatusEnum;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.engineModule.factory.GameEngineFactory;
import com.codemonks.gameservice.engineModule.model.BoardLayout;
import com.codemonks.gameservice.entity.GameConfigEntity;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.TambolaRuleConfigEntity;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomStatusEnum;
import com.codemonks.gameservice.enums.TambolaRuleTypeEnum;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.mapper.LobbyMapper;
import com.codemonks.gameservice.mapper.RoomMapper;
import com.codemonks.gameservice.repository.GameConfigEntityRepository;
import com.codemonks.gameservice.repository.PlayerEntityRepository;
import com.codemonks.gameservice.repository.RoomEntityRepository;
import com.codemonks.gameservice.repository.TambolaRuleConfigRepository;
import com.codemonks.gameservice.service.BotService;
import com.codemonks.gameservice.service.GameService;
import com.codemonks.gameservice.service.RoomService;
import com.codemonks.gameservice.service.gameroom.factory.GameRoomStrategyFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.*;
import static com.codemonks.gameservice.enums.RoomStatusEnum.ACTIVE;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomEntityRepository roomRepository;
    private final PlayerEntityRepository playerRepository;
    private final GameConfigEntityRepository gameConfigRepository;
    private final TambolaRuleConfigRepository tambolaRuleConfigRepository;
    private final GameService gameService;
    private final GameEngineFactory gameEngineFactory;
    private final BotService botService;
    private final GameRoomStrategyFactory gameRoomStrategyFactory;
    private final ObjectMapper objectMapper;

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

        log.info("Game starting. roomId={}, gameType={}, matchType={}",
                room.getId(), room.getGameType(), room.getMatchType());

        if (room.getGameType() == GameTypeEnum.TAMBOLA) {
            log.info("[TAMBOLA_START_FLOW] Room:{} RoomCode:{} Players:{}",
                    room.getId(), room.getRoomCode(), players.size());
        }

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

    /**
     * Saves the host's rule configuration and syncs it to the Tambola Engine.
     *
     * <p><b>Concurrency:</b> uses {@code findByRoomCodeForUpdate} (pessimistic
     * write lock, {@code SELECT ... FOR UPDATE}) rather than the plain
     * {@code findByRoomCode}. This serializes any two calls for the same room —
     * a second concurrent call (double-click on Save, or a client retry after
     * a slow/timed-out response) BLOCKS at the database until the first one
     * finishes and commits. When it resumes, it sees the row exactly as the
     * first call left it and is rejected by the "already saved" check below,
     * instead of racing to write a second, possibly-different rule set. This
     * needs no schema change — RoomEntity is shared across Ludo/TicTacToe/
     * Tambola, so the fix is scoped entirely to this method's query, not to
     * the entity itself.</p>
     *
     * <p><b>Idempotency:</b> per product decision, Save is one-time/locked —
     * a room's rules cannot be edited once saved (a new room is required to
     * reconfigure). {@code ruleConfigJson != null} is therefore treated as
     * "already locked."</p>
     */
    @Transactional
    @Override
    public void setRoomRules(String roomCode, SetRoomRulesRequestDTO request) {

        RoomEntity room = roomRepository.findByRoomCodeForUpdate(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        if (room.getGameType() != GameTypeEnum.TAMBOLA) {
            throw new GameException(RULES_NOT_ALLOWED_FOR_GAME);
        }

        if (room.getStatus() != RoomStatusEnum.WAITING) {
            throw new GameException(GAME_ALREADY_STARTED);
        }

        // Idempotency guard: rules are write-once. This also protects against
        // the double-submit race described above, now that the row is locked.
        if (room.getRuleConfigJson() != null) {
            log.warn("[RULES_ALREADY_LOCKED] roomId={} roomCode={} - rejecting duplicate/second save attempt",
                    room.getId(), roomCode);
            throw new GameException(RULES_ALREADY_LOCKED);
        }

        PlayerEntity host = playerRepository.findByRoom_IdAndUserId(room.getId(), request.getHostUserId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (host.getRole() != RoomPlayerRole.HOST) {
            throw new GameException(ONLY_HOST_CAN_START_GAME);
        }

        GameConfigEntity config = gameConfigRepository
                .findByIdTenantIdAndIdGameType(room.getTenantId(), room.getGameType())
                .orElseThrow(() -> new GameException(GAME_CONFIG_NOT_FOUND));

        List<ResolvedRuleConfig> resolvedRules =
                validateAndResolveRuleConfigs(request.getRules(), config.getMaxPlayers());

        try {
            String json = objectMapper.writeValueAsString(resolvedRules);
            room.setRuleConfigJson(json);
            roomRepository.save(room);
        } catch (JsonProcessingException e) {
            throw new GameException(INVALID_REQUEST);
        }

        log.info("Rules saved. roomId={}, ruleCount={}", room.getId(), resolvedRules.size());

        // Tambola ke liye rule snapshot ko engine ke through Supabase
        // (realtime_tambola_rules) me bhi sync karo — frontend direct-upsert
        // ke bina bhi har client ko host ke latest selection mile.
        gameEngineFactory.getStrategy(room.getGameType()).replaceRules(
                room.getId(),
                room.getRoomCode(),
                resolvedRules.stream()
                        .map(r -> new com.codemonks.gameservice.engineModule.dto.request.TambolaRuleConfigRequestDTO(
                                r.getRuleType(), r.getOrder(), r.getMaxWinners(), r.getThreshold()))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Validates the host-provided rule configuration and resolves each rule's
     * threshold from the MySQL master table (client-supplied threshold is ignored).
     */
    private List<ResolvedRuleConfig> validateAndResolveRuleConfigs(
            List<SetRoomRulesRequestDTO.RuleSelection> selections,
            Integer maxPlayersUpperBound) {

        if (selections == null || selections.isEmpty()) {
            throw new GameException(RULES_REQUIRED);
        }

        List<ResolvedRuleConfig> result = new ArrayList<>();
        Set<String> seenRuleTypes = new HashSet<>();
        Set<Integer> seenOrders = new HashSet<>();

        // RuleType valid & active hota hai agar wo master table (tambola_rule_config) me exist karta hai.
        for (SetRoomRulesRequestDTO.RuleSelection selection : selections) {

            if (selection.getRuleType() == null || selection.getRuleType().isBlank()) {
                throw new GameException(RULE_TYPE_NOT_FOUND);
            }

            TambolaRuleTypeEnum knownType = TambolaRuleTypeEnum.fromName(selection.getRuleType());
            if (knownType == null) {
                throw new GameException(RULE_TYPE_NOT_FOUND);
            }

            if (!seenRuleTypes.add(knownType.name())) {
                throw new GameException(DUPLICATE_RULE_TYPE);
            }

            TambolaRuleConfigEntity masterRule = tambolaRuleConfigRepository
                    .findByRuleType(knownType.name())
                    .orElseThrow(() -> new GameException(RULE_TYPE_NOT_FOUND));

            if (selection.getOrder() == null || selection.getOrder() < 1) {
                throw new GameException(INVALID_RULE_ORDER);
            }
            if (!seenOrders.add(selection.getOrder())) {
                throw new GameException(INVALID_RULE_ORDER);
            }

            if (selection.getMaxWinners() == null
                    || selection.getMaxWinners() < 1
                    || (maxPlayersUpperBound != null && selection.getMaxWinners() > maxPlayersUpperBound)) {
                throw new GameException(INVALID_MAX_WINNERS);
            }

            result.add(new ResolvedRuleConfig(
                    knownType.name(),
                    selection.getOrder(),
                    selection.getMaxWinners(),
                    masterRule.getThreshold()
            ));
        }

        // Order 1 se start hokar strictly sequential (no gaps) honi chahiye.
        List<Integer> orders = result.stream()
                .map(ResolvedRuleConfig::getOrder)
                .sorted()
                .toList();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i) != i + 1) {
                throw new GameException(INVALID_RULE_ORDER);
            }
        }

        return result;
    }

    private static class ResolvedRuleConfig {
        private final String ruleType;
        private final Integer order;
        private final Integer maxWinners;
        private final Integer threshold;

        ResolvedRuleConfig(String ruleType, Integer order, Integer maxWinners, Integer threshold) {
            this.ruleType = ruleType;
            this.order = order;
            this.maxWinners = maxWinners;
            this.threshold = threshold;
        }

        public String getRuleType() { return ruleType; }
        public Integer getOrder() { return order; }
        public Integer getMaxWinners() { return maxWinners; }
        public Integer getThreshold() { return threshold; }
    }

    @Transactional(readOnly = true)
    @Override
    public List<TambolaAvailableRuleResponseDTO> getRoomRules(String roomCode) {

        log.info("Fetching rules for roomCode={}", roomCode);

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        if (room.getGameType() != GameTypeEnum.TAMBOLA) {
            throw new GameException(RULES_NOT_ALLOWED_FOR_GAME);
        }

        // Master rules MySQL se — room-specific selection (order/maxWinners)
        // intentionally nahi, sirf available master rules return hote hain.
        List<TambolaRuleConfigEntity> masterRules = tambolaRuleConfigRepository.findAll();

        return masterRules.stream()
                .sorted(Comparator.comparingInt(m -> tambolaRuleTypeIndex(m.getRuleType())))
                .map(m -> {
                    TambolaRuleTypeEnum meta = TambolaRuleTypeEnum.fromName(m.getRuleType());
                    return TambolaAvailableRuleResponseDTO.builder()
                            .ruleType(m.getRuleType())
                            .displayName(meta != null ? meta.getDisplayName() : m.getRuleType())
                            .description(meta != null ? meta.getDescription() : null)
                            .threshold(m.getThreshold())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Canonical order: EARLY_FIVE, TOP_LINE, MIDDLE_LINE, BOTTOM_LINE, FULL_HOUSE
    private int tambolaRuleTypeIndex(String ruleType) {
        TambolaRuleTypeEnum meta = TambolaRuleTypeEnum.fromName(ruleType);
        return meta != null ? meta.ordinal() : Integer.MAX_VALUE;
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