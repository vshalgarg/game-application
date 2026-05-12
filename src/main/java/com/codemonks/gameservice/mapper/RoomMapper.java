package com.codemonks.gameservice.mapper;


import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.dto.request.JoinRoomRequestDTO;
import com.codemonks.gameservice.dto.response.PlayerResponseDTO;
import com.codemonks.gameservice.dto.response.RoomDetailsResponseDTO;
import com.codemonks.gameservice.dto.response.RoomResponseDTO;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.RoomPlayerEntity;
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
        return room;
    }

    public static RoomPlayerEntity toHostPlayer(CreateRoomRequestDTO request, String roomCode) {
        RoomPlayerEntity player = new RoomPlayerEntity();
        player.setTenantId(request.getTenantId());
        player.setRoomCode(roomCode);
        player.setUserId(request.getUserId());
        player.setRole(RoomPlayerRole.HOST);
        player.setStatus(RoomPlayerStatus.ACTIVE);
        return player;
    }

    public static RoomPlayerEntity toJoinPlayer(JoinRoomRequestDTO request, String roomCode) {
        RoomPlayerEntity player = new RoomPlayerEntity();
        player.setTenantId(request.getTenantId());
        player.setRoomCode(roomCode);
        player.setUserId(request.getUserId());
        player.setRole(RoomPlayerRole.PLAYER);
        player.setStatus(RoomPlayerStatus.ACTIVE);
        return player;
    }

    public static RoomResponseDTO toRoomResponse(RoomEntity room) {
        return RoomResponseDTO.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .gameType(room.getGameType())
                .status(room.getStatus().name())
                .build();
    }

    public static PlayerResponseDTO toPlayerResponseDTO(
            RoomPlayerEntity entity
    ) {

        return PlayerResponseDTO.builder()
                .userId(entity.getUserId())
                .role(entity.getRole())
                .status(entity.getStatus())
                .build();
    }

    public static RoomDetailsResponseDTO toRoomDetailsResponseDTO(
            RoomEntity room,
            List<RoomPlayerEntity> players
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
}