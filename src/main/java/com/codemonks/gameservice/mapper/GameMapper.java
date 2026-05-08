package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDto;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import com.codemonks.gameservice.entity.GameEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.entity.RoomPlayerEntity;

import java.util.List;

public class GameMapper {

    public static EngineStartGameRequestDto toStartGameRequest(
            RoomEntity room,
            List<RoomPlayerEntity> players
    ) {
        List<Long> playerIds = players.stream()
                .map(RoomPlayerEntity::getUserId)
                .toList();

        return EngineStartGameRequestDto.builder()
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .gameType(room.getGameType())
                .playerIds(playerIds)
                .build();
    }

        public static RealtimeGameStateDTO toRealtimeState(
                GameEntity game,
                String roomCode,
                EngineGameStateResponseDTO response
        ) {

            return RealtimeGameStateDTO.builder()
                    .gameId(game.getId())
                    .roomCode(roomCode)
                    .boardState(response.getBoardState())
                    .currentTurnUserId(
                            response.getCurrentTurnUserId()
                    )
                    .status(response.getStatus())
                    .winnerUserId(
                            response.getWinnerUserId()
                    )
                    .players(response.getPlayers())
                    .build();
        }
}
