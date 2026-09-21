package com.codemonks.gameservice.mapper;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.dto.request.SetRoomRulesRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaPlayerRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaRuleConfigRequestDTO;
import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.entity.RoomEntity;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.exceptions.GameException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Comparator;
import java.util.List;

public final class GameMapper {

    private GameMapper() {}

    public static EngineStartGameRequestDTO toStartGameRequest(
            RoomEntity room,
            List<PlayerEntity> players) {

        List<Long> playerIds = players.stream()
                .sorted(Comparator.comparing(
                        p -> p.getRole() == RoomPlayerRole.HOST ? 0 : 1))
                .map(PlayerEntity::getUserId)
                .toList();

        EngineStartGameRequestDTO.EngineStartGameRequestDTOBuilder builder =
                EngineStartGameRequestDTO.builder()
                        .roomId(room.getId())
                        .roomCode(room.getRoomCode())
                        .gameType(room.getGameType())
                        .playerIds(playerIds)
                        .matchType(room.getMatchType())
                        .botDifficulty(room.getBotDifficulty());

         if (room.getGameType() == GameTypeEnum.TAMBOLA) {
            builder.timerIntervalSeconds(2); // TODO: agar host isko choose kar sake, room-config se lo

            List<TambolaPlayerRequestDTO> tambolaPlayers = players.stream()
                    .map(p -> new TambolaPlayerRequestDTO(
                            p.getUserId(),
                            1, // TODO: per-player ticket-count agar host choose kar sake, yahan se lo
                            p.getRole() == RoomPlayerRole.BOT))
                    .toList();
            builder.players(tambolaPlayers);
            builder.rules(parseRules(room.getRuleConfigJson()));
        }
        return builder.build();
    }

     private static List<TambolaRuleConfigRequestDTO> parseRules(String ruleConfigJson) {
        if (ruleConfigJson == null || ruleConfigJson.isBlank()) {
            throw new GameException(ResponseErrorCodes.RULES_NOT_CONFIGURED);
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<SetRoomRulesRequestDTO.RuleSelection> raw = mapper.readValue(
                    ruleConfigJson, new TypeReference<>() {});
            return raw.stream()
                    .map(r -> new TambolaRuleConfigRequestDTO(
                            r.getRuleType(), r.getOrder(), r.getMaxWinners(), r.getThreshold()))
                    .toList();
        } catch (JsonProcessingException e) {
            throw new GameException(ResponseErrorCodes.INVALID_REQUEST);
        }
    }
}