package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDto;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;

import java.util.List;

public final class GameMapper {

    public static EngineStartGameRequestDto toStartGameRequest(
            RoomEntity room,
            List<PlayerEntity> players
    ) {

        List<Long> playerIds = players.stream()
                .map(PlayerEntity::getUserId)
                .toList();

        return EngineStartGameRequestDto.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .gameType(room.getGameType())
                .playerIds(playerIds)
                .matchType(room.getMatchType())
                .botDifficulty(room.getBotDifficulty())
                .build();
    }

    public static RealtimeGameStateDTO toRealtimeState(
            RoomEntity room,
            EngineGameStateResponseDTO response,
            Long version
    ) {

        return RealtimeGameStateDTO.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .gameState(response.getGameState())
                .currentTurnUserId(response.getCurrentTurnUserId())
                .gameStatus(response.getStatus().name())
                .winnerUserId(response.getWinnerUserId())
                .version(version)
                .build();
    }
}