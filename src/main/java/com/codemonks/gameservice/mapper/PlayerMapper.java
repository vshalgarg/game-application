package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.common.PlayerDto;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.enums.PlayerSideEnum;
import com.codemonks.gameservice.enums.RoomPlayerRole;

import java.util.List;

public final class PlayerMapper {

    private PlayerMapper() {}

    public static List<PlayerDto> toPlayerDtos(
            List<PlayerEntity> players
    ) {

        return players.stream()
                .map(PlayerMapper::toPlayerDto)
                .toList();
    }

    public static PlayerDto toPlayerDto(
            PlayerEntity player
    ) {

        boolean isHost =
                player.getRole() == RoomPlayerRole.HOST;

        return PlayerDto.builder()
                .userId(player.getUserId())
                .turnOrder(isHost ? 1 : 2)
                .side(
                        isHost
                                ? PlayerSideEnum.X.name()
                                : PlayerSideEnum.O.name()
                )
                .isBot(false)
                .build();
    }
}