package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.enums.RoomRealtimeStatusEnum;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;

import java.util.List;

public final class LobbyMapper {

    private LobbyMapper() {
    }

    public static RealtimeLobbyDTO toLobbyDTO(
            RoomEntity room,
            List<PlayerEntity> players,
            RoomRealtimeStatusEnum status
    ) {
        return RealtimeLobbyDTO.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .roomStatus(status.name())
                .players(PlayerMapper.toPlayerDtos(players))
                .build();
    }
}

