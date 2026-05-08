package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.entity.RoomPlayerEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;

import java.util.List;

public class PlayerMapper {

    public static List<PlayerDto> toPlayerDtos(
            List<RoomPlayerEntity> roomPlayers
    ) {

        return roomPlayers.stream()
                .map(PlayerMapper::toPlayerDto)
                .toList();
    }

    private static PlayerDto toPlayerDto(
            RoomPlayerEntity player
    ) {

        boolean isHost =
                player.getRole() == RoomPlayerRole.HOST;

        return PlayerDto.builder()
                .userId(player.getUserId())
                .turnOrder(isHost ? 1 : 2)
                .side(isHost ? "X" : "O")
                .build();
    }
}
