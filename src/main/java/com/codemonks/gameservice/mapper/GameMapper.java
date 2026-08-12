package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;

import java.util.Comparator;
import java.util.List;

public final class GameMapper {

    public static EngineStartGameRequestDTO toStartGameRequest(
            RoomEntity room,
            List<PlayerEntity> players) {

        List<Long> playerIds = players.stream()
                .sorted(Comparator.comparing(
                        p -> p.getRole() == RoomPlayerRole.HOST ? 0 : 1))
                .map(PlayerEntity::getUserId)
                .toList();
        return EngineStartGameRequestDTO.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .gameType(room.getGameType())
                .playerIds(playerIds)
                .matchType(room.getMatchType())
                .botDifficulty(room.getBotDifficulty())
                .build();
    }}
