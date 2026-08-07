package com.codemonks.gameservice.mapper;


import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.dto.request.JoinRoomRequestDTO;
import com.codemonks.gameservice.dto.response.PlayerResponseDTO;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomPlayerStatus;
import com.codemonks.gameservice.enums.RoomStatusEnum;

import java.util.List;

public class RoomMapper {

    private RoomMapper() {}

    public static RoomEntity toRoomEntity(CreateRoomRequestDTO request, String roomCode) {
        RoomEntity room = new RoomEntity();
        room.setTenantId(request.getTenantId());
        room.setGameType(request.getGameType());
        room.setStatus(RoomStatusEnum.WAITING);
        room.setRoomCode(roomCode);
        room.setMatchType(request.getMatchType());
        room.setBotDifficulty(request.getBotDifficulty());
        return room;
    }

    public static PlayerEntity toHostPlayer(CreateRoomRequestDTO request, RoomEntity room) {
        PlayerEntity player = new PlayerEntity();
        player.setTenantId(request.getTenantId());
        player.setRoom(room);
        player.setUserId(request.getUserId());
        player.setRole(RoomPlayerRole.HOST);
        player.setStatus(RoomPlayerStatus.ACTIVE);
        return player;
    }

    public static PlayerEntity toJoinPlayer(JoinRoomRequestDTO request, RoomEntity room) {
        PlayerEntity player = new PlayerEntity();
        player.setTenantId(request.getTenantId());
        player.setRoom(room);
        player.setUserId(request.getUserId());
        player.setRole(RoomPlayerRole.PLAYER);
        player.setStatus(RoomPlayerStatus.ACTIVE);
        return player;
    }

    public static RoomResponseDTO toRoomResponse(RoomEntity room, PlayerEntity player) {
        return RoomResponseDTO.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .userId(player.getUserId())
                .gameType(room.getGameType())
                .status(room.getStatus().name())
                .role(player.getRole())
                .build();
    }

    public static PlayerResponseDTO toPlayerResponseDTO(
            PlayerEntity entity
    ) {

        return PlayerResponseDTO.builder()
                .userId(entity.getUserId())
                .role(entity.getRole())
                .status(entity.getStatus())
                .build();
    }

    public static RoomDetailsResponseDTO toRoomDetailsResponseDTO(
            RoomEntity room,
            List<PlayerEntity> players
    ) {

        return RoomDetailsResponseDTO.builder()
                .roomId(room.getId())
                .tenantId(room.getTenantId())
                .roomCode(room.getRoomCode())
                .gameType(room.getGameType())
                .status(room.getStatus())
                .players(
                        players.stream()
                                .map(RoomMapper::toPlayerResponseDTO)
                                .toList()
                )
                .build();
    }
    public static PlayerEntity toBotPlayer(
            String tenantId,
            RoomEntity room,
            Long botUserId
    ) {

        PlayerEntity player = new PlayerEntity();
        player.setTenantId(tenantId);
        player.setRoom(room);
        player.setUserId(botUserId);
        player.setRole(RoomPlayerRole.BOT);
        player.setStatus(RoomPlayerStatus.ACTIVE);

        return player;
    }
}