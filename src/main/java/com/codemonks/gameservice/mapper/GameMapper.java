package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDto;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;

import java.util.List;

public class GameMapper {

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
                .build();
    }

        public static RealtimeGameStateDTO toRealtimeState(
                RoomEntity room,
                EngineGameStateResponseDTO response
        ) {

            return RealtimeGameStateDTO.builder()
                    .roomId(room.getId())
                    .roomCode(room.getRoomCode())
                    .boardState(response.getBoardState())
                    .currentTurnUserId(
                            response.getCurrentTurnUserId()
                    )
                    .gameState(String.valueOf(response.getStatus()))
                    .winnerUserId(
                            response.getWinnerUserId()
                    )
                    .players(response.getPlayers())
                    .build();
        }
}
