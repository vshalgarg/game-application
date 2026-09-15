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

/**
 * GameMapper — RoomEntity + players ko engine ke /start-game call ke liye
 * expected EngineStartGameRequestDTO me convert karta hai.
 * <p>
 * Baaki games (Ludo, TicTacToe) ke liye common fields hi kaafi hain.
 * Tambola ke liye extra fields (players-with-tickets, rules) bhi bharne
 * padte hain — wo block sirf gameType == TAMBOLA hone par activate hota hai,
 * baaki games is naye code se untouched rehte hain.
 */
public final class GameMapper {

    private GameMapper() {}

    /**
     * @param room    room jiska game start ho raha hai
     * @param players is room ke saare active players (host + joined + bots)
     * @return engine ko bhejne layak EngineStartGameRequestDTO
     */
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

        // Tambola-specific fields sirf tab bharo jab gameType TAMBOLA ho —
        // Ludo/TicTacToe ke liye ye extra fields bilkul touch nahi hote.
        if (room.getGameType() == GameTypeEnum.TAMBOLA) {
            builder.timerIntervalSeconds(5); // TODO: agar host isko choose kar sake, room-config se lo

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

    /**
     * RoomServiceImpl.setRoomRules() ne jo rule-selection JSON
     * ruleConfigJson me save kiya tha, usko wapas parse karke engine ke
     * TambolaRuleConfigRequestDTO list me convert karta hai.
     * <p>
     * Ye method sirf "draft se authoritative tak transport" karta hai —
     * koi rule-validation yahan nahi hoti (wo host ke set-rules call ke
     * waqt already ho chuki hoti hai).
     *
     * @param ruleConfigJson RoomEntity.ruleConfigJson me stored raw JSON
     * @return engine ko bhejne layak rule-config list
     * @throws GameException agar rules abhi set hi nahi hue (RULES_NOT_CONFIGURED),
     *                        ya saved JSON corrupt/unparseable hai (INVALID_REQUEST)
     */
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