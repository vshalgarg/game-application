package com.codemonks.tambola_engine.repository.impl;

import com.codemonks.tambola_engine.config.SupabaseProperties;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.exception.RoomNotFoundException;
import com.codemonks.tambola_engine.exception.SupabaseStateException;
import com.codemonks.tambola_engine.repository.TambolaGameStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository("tambolaRoomRepositoryImpl")
@RequiredArgsConstructor
public class TambolaGameStateRepositoryImpl
        implements TambolaGameStateRepository {

    private final RestClient tambolaSupabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public TambolaGameState findById(Long roomId) {

        String table =
                properties.getTables().getRealtimeGameState();

        try {

            List<GameStateRow> result =
                    tambolaSupabaseRestClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/rest/v1/" + table)
                                    .queryParam(
                                            "room_id",
                                            "eq." + roomId
                                    )
                                    .queryParam("select", "*")
                                    .build())
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            List<GameStateRow>>() {
                                    }
                            );

            if (result == null || result.isEmpty()) {
                throw new RoomNotFoundException(roomId);
            }

            return result.get(0).toDomain();

        } catch (RoomNotFoundException e) {

            throw e;

        } catch (Exception e) {

            log.error(
                    "Failed to fetch game-state. roomId={}",
                    roomId,
                    e
            );

            throw new SupabaseStateException(
                    "Failed to fetch game-state for roomId=" + roomId,
                    e
            );
        }
    }

    @Override
    public void insert(TambolaGameState state) {

        String table =
                properties.getTables().getRealtimeGameState();

        try {

            GameStateRow row =
                    GameStateRow.fromDomain(state);

            tambolaSupabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=minimal")
                    .body(row)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {

            log.error(
                    "Failed to insert game-state. roomId={}",
                    state.getRoomId(),
                    e
            );

            throw new SupabaseStateException(
                    "Failed to insert game-state for roomId="
                            + state.getRoomId(),
                    e
            );
        }
    }

    @Override
    public boolean updateIfVersionMatches(
            Long roomId,
            Map<String, Object> changes,
            Long expectedVersion
    ) {

        String table =
                properties.getTables().getRealtimeGameState();

        Map<String, Object> body =
                new HashMap<>(changes);

        body.put("version", expectedVersion + 1);
        body.put("updated_at", Instant.now().toString());

        try {

            List<Object> updatedRows =
                    tambolaSupabaseRestClient.patch()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/rest/v1/" + table)
                                    .queryParam(
                                            "room_id",
                                            "eq." + roomId
                                    )
                                    .queryParam(
                                            "version",
                                            "eq." + expectedVersion
                                    )
                                    .build())
                            .header(
                                    "Prefer",
                                    "return=representation"
                            )
                            .body(body)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            List<Object>>() {
                                    }
                            );

            return updatedRows != null
                    && !updatedRows.isEmpty();

        } catch (Exception e) {

            log.error(
                    "Failed to update game-state. roomId={}",
                    roomId,
                    e
            );

            throw new SupabaseStateException(
                    "Failed to update game-state for roomId="
                            + roomId,
                    e
            );
        }
    }

    @Override
    public List<TambolaGameState> findRoomsDueForTick(
            Instant now
    ) {

        String table =
                properties.getTables().getRealtimeGameState();

        try {

            List<GameStateRow> result =
                    tambolaSupabaseRestClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/rest/v1/" + table)
                                    .queryParam(
                                            "game_status",
                                            "eq."
                                                    + GameStatusEnum
                                                    .RUNNING
                                                    .name()
                                    )
                                    .queryParam("select", "*")
                                    .build())
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            List<GameStateRow>>() {
                                    }
                            );

            if (result == null) {
                return List.of();
            }

            /*
             * next_tick_at is stored inside game_state_data JSONB,
             * so filtering is performed on the Java side.
             */
            return result.stream()
                    .map(GameStateRow::toDomain)
                    .filter(state ->
                            state.getNextTickAt() != null
                                    && !state.getNextTickAt()
                                    .isAfter(now)
                    )
                    .toList();

        } catch (Exception e) {

            log.error(
                    "Failed to poll due-rooms",
                    e
            );

            throw new SupabaseStateException(
                    "Failed to poll due-rooms",
                    e
            );
        }
    }

    /**
     * Representation of the realtime_game_state row.
     *
     * Supabase columns:
     *
     * room_id
     * room_code
     * game_state_data
     * players
     * game_status
     * version
     */
    private record GameStateRow(

            Long room_id,

            String room_code,

            Map<String, Object> game_state_data,

            List<PlayerDTO> players,

            String game_status,

            Long version

    ) {

        /**
         * Converts Supabase row into Tambola domain state.
         */
        TambolaGameState toDomain() {

            Map<String, Object> stateData =
                    game_state_data != null
                            ? game_state_data
                            : Map.of();

            /*
             * Jackson may deserialize JSON numbers as different
             * numeric types, so convert them explicitly to Integer.
             */
            List<Integer> calledNumbers =
                    extractCalledNumbers(stateData);

            Integer timerIntervalSeconds =
                    extractInteger(
                            stateData,
                            "timer_interval_seconds"
                    );

            String nextTickAtStr =
                    extractString(
                            stateData,
                            "next_tick_at"
                    );

            /*
             * players is already represented as List<PlayerDTO>.
             *
             * Never return null because the database column is
             * NOT NULL and runtime code should also work with an
             * empty player list.
             */
            List<PlayerDTO> statePlayers =
                    players != null
                            ? players
                            : List.of();

            return TambolaGameState.builder()
                    .roomId(room_id)
                    .roomCode(room_code)
                    .status(
                            GameStatusEnum.valueOf(game_status)
                    )
                    .calledNumbers(calledNumbers)
                    .timerIntervalSeconds(
                            timerIntervalSeconds
                    )
                    .nextTickAt(
                            nextTickAtStr != null
                                    ? Instant.parse(nextTickAtStr)
                                    : null
                    )
                    .players(statePlayers)
                    .version(version)
                    .build();
        }

        /**
         * Converts Tambola domain state into the Supabase row.
         */
        static GameStateRow fromDomain(
                TambolaGameState state
        ) {

            Map<String, Object> blob =
                    new HashMap<>();

            /*
             * game_state_data JSONB
             */
            blob.put(
                    "called_numbers",
                    state.getCalledNumbers() != null
                            ? state.getCalledNumbers()
                            : List.of()
            );

            blob.put(
                    "timer_interval_seconds",
                    state.getTimerIntervalSeconds()
            );

            blob.put(
                    "next_tick_at",
                    state.getNextTickAt() != null
                            ? state.getNextTickAt().toString()
                            : null
            );

            /*
             * IMPORTANT:
             *
             * realtime_game_state.players is NOT NULL.
             *
             * Therefore we must NEVER send null here.
             */
            List<PlayerDTO> players =
                    state.getPlayers() != null
                            ? state.getPlayers()
                            : List.of();

            return new GameStateRow(
                    state.getRoomId(),
                    state.getRoomCode(),
                    blob,
                    players,
                    state.getStatus().name(),
                    state.getVersion()
            );
        }

        /**
         * Safely extracts called_numbers from game_state_data.
         */
        private static List<Integer> extractCalledNumbers(
                Map<String, Object> stateData
        ) {

            Object value =
                    stateData.get("called_numbers");

            if (!(value instanceof List<?> rawList)) {
                return List.of();
            }

            List<Integer> numbers =
                    new ArrayList<>();

            for (Object item : rawList) {

                if (item instanceof Number number) {
                    numbers.add(number.intValue());
                }
            }

            return numbers;
        }

        /**
         * Safely extracts an Integer from JSONB data.
         */
        private static Integer extractInteger(
                Map<String, Object> stateData,
                String key
        ) {

            Object value = stateData.get(key);

            if (value instanceof Number number) {
                return number.intValue();
            }

            return null;
        }

        /**
         * Safely extracts a String from JSONB data.
         */
        private static String extractString(
                Map<String, Object> stateData,
                String key
        ) {

            Object value = stateData.get(key);

            return value != null
                    ? value.toString()
                    : null;
        }
    }
}