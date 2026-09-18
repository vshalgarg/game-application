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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository("tambolaRoomRepositoryImpl")
@RequiredArgsConstructor
public class TambolaGameStateRepositoryImpl implements TambolaGameStateRepository {

    private final RestClient tambolaSupabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public TambolaGameState findById(Long roomId) {
        String table = properties.getTables().getRealtimeTambolaGameState();

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
                properties.getTables().getRealtimeTambolaGameState();

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

        String table = properties.getTables().getRealtimeTambolaGameState();

        Map<String, Object> body = new HashMap<>(changes);
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

            if (updatedRows != null && !updatedRows.isEmpty()) {
                log.info("[TAMBOLA_STATE_UPDATED] roomId={} version={}->{} keys={}",
                        roomId, expectedVersion, expectedVersion + 1, body.keySet());
                return true;
            }

            log.warn("[TAMBOLA_STATE_STALE] roomId={} expectedVersion={} - update skipped (concurrent write or already advanced)",
                    roomId, expectedVersion);
            return false;

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
                properties.getTables().getRealtimeTambolaGameState();

        try {

            List<GameStateRow> result =
                    tambolaSupabaseRestClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/rest/v1/" + table)
                                    .queryParam(
                                            "game_status",
                                            "in.("
                                                    + GameStatusEnum
                                                    .RUNNING
                                                    .name()
                                                    + ","
                                                    + GameStatusEnum
                                                    .WIN
                                                    .name()
                                                    + ")"
                                    )
                                    .queryParam(
                                            "next_tick_at",
                                            "lte." + now.toString()
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
             * next_tick_at is now a real TIMESTAMPTZ column, so the
             * due-rooms filter runs server-side (next_tick_at=lte.now).
             * The Java-side filter below is kept as a safety fallback.
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

    private record GameStateRow(
            Long room_id,
            String room_code,
            String game_status,
            List<Integer> called_numbers,
            Integer timer_interval_seconds,
            String next_tick_at,
            List<PlayerDTO> players,
            Long version
    ) {

        TambolaGameState toDomain() {

            /*
             * players is never null because the database column
             * is NOT NULL and runtime code should also work with
             * an empty player list.
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
                    .calledNumbers(
                            called_numbers != null
                                    ? called_numbers
                                    : List.of()
                    )
                    .timerIntervalSeconds(
                            timer_interval_seconds
                    )
                    .nextTickAt(
                            next_tick_at != null
                                    ? Instant.parse(next_tick_at)
                                    : null
                    )
                    .players(statePlayers)
                    .version(version)
                    .build();
        }

        /**
         * Converts Tambola domain state into a dedicated
         * realtime_tambola_game_state row (one field per column).
         */
        static GameStateRow fromDomain(
                TambolaGameState state
        ) {

            /*
             * IMPORTANT:
             *
             * realtime_tambola_game_state.players is NOT NULL.
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
                    state.getStatus().name(),
                    state.getCalledNumbers() != null
                            ? state.getCalledNumbers()
                            : List.of(),
                    state.getTimerIntervalSeconds(),
                    state.getNextTickAt() != null
                            ? state.getNextTickAt().toString()
                            : null,
                    players,
                    state.getVersion()
            );
        }
    }
}