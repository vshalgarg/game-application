package com.codemonks.ludo_game_engine.service.Impl;

import com.codemonks.ludo_game_engine.config.SupabaseProperties;
import com.codemonks.ludo_game_engine.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.ludo_game_engine.dto.realtime.RealtimeMoveDTO;
import com.codemonks.ludo_game_engine.exception.SupabaseStateException;
import com.codemonks.ludo_game_engine.service.SupabaseRealtimeService;
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

    @Override
    public RealtimeGameStateDTO getGameState(Long roomId) {
        String table = properties.getTables().getRealtimeGameState();
        try {
            List<RealtimeGameStateDTO> result = supabaseRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("room_id", "eq." + roomId)
                            .queryParam("select", "*")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RealtimeGameStateDTO>>() {});

            if (result == null || result.isEmpty()) {
                throw new SupabaseStateException("No game state found for roomId=" + roomId);
            }
            return result.get(0);
        } catch (SupabaseStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch game state from Supabase. roomId={}", roomId, e);
            throw new SupabaseStateException("Failed to fetch game state for roomId=" + roomId, e);
        }
    }

    @Override
    public void upsertGameState(RealtimeGameStateDTO state) {
        String table = properties.getTables().getRealtimeGameState();
        try {
            supabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "resolution=merge-duplicates,return=minimal")
                    .body(state)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to upsert game state to Supabase. roomId={}", state.getRoomId(), e);
            throw new SupabaseStateException("Failed to upsert game state for roomId=" + state.getRoomId(), e);
        }
    }

    @Override
    public void saveMove(RealtimeMoveDTO moveDTO) {
        String table = properties.getTables().getRealtimeGameMoves();
        try {
            supabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=minimal")
                    .body(moveDTO)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e)   {
            log.error("Failed to save move to Supabase. roomId={}", moveDTO.getRoomId(), e);
            throw new SupabaseStateException("Failed to save move for roomId=" + moveDTO.getRoomId(), e);
        }
    }
}