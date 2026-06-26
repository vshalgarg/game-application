package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.common.PlayerDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.LobbyPlayerDTO;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.enums.PlayerSideEnum;
import com.codemonks.gameservice.enums.RoomPlayerRole;

import java.util.List;

public final class PlayerMapper {

    private PlayerMapper() {}

    public static List<PlayerDTO> toPlayerDtos(
            List<PlayerEntity> players
    ) {

        return players.stream()
                .map(PlayerMapper::toPlayerDto)
                .toList();
    }

    public static PlayerDTO toPlayerDto(
            PlayerEntity player
    ) {

        boolean isHost =
                player.getRole() == RoomPlayerRole.HOST;

        return PlayerDTO.builder()
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


    public static List<LobbyPlayerDTO> toLobbyPlayerDtos(
            List<PlayerEntity> players
    ) {

        return players.stream()
                .map(PlayerMapper::toLobbyPlayerDto)
                .toList();
    }

    public static LobbyPlayerDTO toLobbyPlayerDto(
            PlayerEntity player
    ) {

        return LobbyPlayerDTO.builder()
                .userId(player.getUserId())
                .role(player.getRole().name())
                .status(player.getStatus().name())
                .build();
    }
}