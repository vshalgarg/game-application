package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.dto.request.CreateRoomRequest;
import com.codemonks.gameservice.dto.request.JoinRoomRequest;
import com.codemonks.gameservice.dto.response.RoomResponse;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.RoomPlayerEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.enums.RoomPlayerStatus;
import com.codemonks.gameservice.enums.RoomStatusEnum;

public class RoomMapper {

    private RoomMapper() {}

    public static RoomEntity toRoomEntity(CreateRoomRequest request, String roomCode) {
        RoomEntity room = new RoomEntity();
        room.setTenantId(request.getTenantId());
        room.setGameType(request.getGameType());
        room.setStatus(RoomStatusEnum.WAITING);
        room.setRoomCode(roomCode);
        return room;
    }

    public static RoomPlayerEntity toHostPlayer(CreateRoomRequest request, Long roomId) {
        RoomPlayerEntity player = new RoomPlayerEntity();
        player.setTenantId(request.getTenantId());
        player.setRoomId(roomId);
        player.setUserId(request.getUserId());
        player.setRole(RoomPlayerRole.HOST);
        player.setStatus(RoomPlayerStatus.ACTIVE);
        return player;
    }

    public static RoomPlayerEntity toJoinPlayer(JoinRoomRequest request, Long roomId) {
        RoomPlayerEntity player = new RoomPlayerEntity();
        player.setTenantId(request.getTenantId());
        player.setRoomId(roomId);
        player.setUserId(request.getUserId());
        player.setRole(RoomPlayerRole.PLAYER);
        player.setStatus(RoomPlayerStatus.ACTIVE);
        return player;
    }

    public static RoomResponse toRoomResponse(RoomEntity room) {
        return RoomResponse.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .gameType(room.getGameType())
                .status(room.getStatus().name())
                .build();
    }
}