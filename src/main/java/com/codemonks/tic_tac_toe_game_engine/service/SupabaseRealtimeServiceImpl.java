package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.config.SupabaseProperties;
import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeMoveDTO;
import com.codemonks.tic_tac_toe_game_engine.exception.SupabaseStateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseRealtimeServiceImpl implements SupabaseRealtimeService {

    private final RestClient supabaseRestClient;
    private final SupabaseProperties properties;


    private String getGameStateTable() {

        if (properties.getTables() == null) {
            throw new IllegalStateException(
                    "Missing configuration: supabase.tables"
            );
        }

        return properties.getTables().getRealtimeGameState();
    }

    private String getMoveTable() {

        if (properties.getTables() == null) {
            throw new IllegalStateException(
                    "Missing configuration: supabase.tables"
            );
        }

        return properties.getTables().getRealtimeGameMoves();
    }

    @Override
    public RealtimeGameStateDTO getGameState(Long roomId) {

        String table = getGameStateTable();

        try {

            List<RealtimeGameStateDTO> result =
                    supabaseRestClient.get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path("/rest/v1/" + table)
                                            .queryParam("room_id", "eq." + roomId)
                                            .queryParam("select", "*")
                                            .build())
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<RealtimeGameStateDTO>>() {});

            if (result == null || result.isEmpty()) {
                throw new SupabaseStateException(
                        "No game state found for roomId=" + roomId
                );
            }

            return result.get(0);

        } catch (SupabaseStateException exception) {

            throw exception;

        } catch (Exception e) {

            log.error(
                    "Failed to fetch game state. roomId={}",
                    roomId,
                    e
            );

            throw new SupabaseStateException(
                    "Failed to fetch game state for roomId=" + roomId,
                    e
            );
        }
    }

    @Override
    public void upsertGameState(
            RealtimeGameStateDTO state
    ) {

        String table =
                properties.getTables().getRealtimeGameState();

        try {

            supabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header(
                            "Prefer",
                            "resolution=merge-duplicates,return=minimal"
                    )
                    .body(state)
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "[GAME_STATE_UPSERTED] Room:{} Sequence:{}",
                    state.getRoomId(),
                    state.getStateSequence()
            );

        } catch (Exception e) {

            log.error(
                    "Failed to upsert game state. roomId={}",
                    state.getRoomId(),
                    e
            );

            throw new SupabaseStateException(
                    "Failed to upsert game state for roomId="
                            + state.getRoomId(),
                    e
            );
        }
    }

    @Override
    public void saveMove(
            RealtimeMoveDTO moveDTO
    ) {

        String table =
                properties.getTables().getRealtimeGameMoves();

        try {

            supabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=minimal")
                    .body(moveDTO)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception exception) {

            log.error(
                    "Failed to save move. roomId={}",
                    moveDTO.getRoomId(),
                    exception
            );

            throw new SupabaseStateException(
                    "Failed to save move for roomId="
                            + moveDTO.getRoomId(),
                    exception
            );
        }
    }
}