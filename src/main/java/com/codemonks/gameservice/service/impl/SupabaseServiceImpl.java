package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.config.SupabaseProperties;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeMoveDTO;
import com.codemonks.gameservice.service.SupabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupabaseServiceImpl implements SupabaseService {

    private final WebClient supabaseWebClient;
    private final SupabaseProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public void createInitialState(
            RealtimeGameStateDTO state
    ) {

        try{
            String jsonPayload = objectMapper.writeValueAsString(state);
            log.info("Supabase JSON payload: {}", jsonPayload);

            supabaseWebClient.post()
                    .uri("/rest/v1/" +
                            properties
                                    .getTables()
                                    .getRealtimeGameState())
                    .header("Prefer", "return=minimal")
                    .bodyValue(state)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("Supabase error response: {}", errorBody);
                                        return new RuntimeException(errorBody);
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Supabase create state failed", e);
            throw new RuntimeException("Failed to create realtime game state");
        }
    }

    @Override
    public RealtimeGameStateDTO getGameState(String gameId) {

        return supabaseWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/rest/v1/" +
                                        properties
                                                .getTables()
                                                .getRealtimeGameState())
                                .queryParam("game_id", "eq." + gameId)
                                .queryParam("select", "*")
                                .build()
                )
                .retrieve()
                .bodyToFlux(RealtimeGameStateDTO.class)
                .blockFirst();
    }

    @Override
    public void updateGameState(
            RealtimeGameStateDTO state
    ) {
        supabaseWebClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/rest/v1/" +
                                        properties
                                                .getTables()
                                                .getRealtimeGameState())
                                .queryParam(
                                        "game_id",
                                        "eq." + state.getGameId()
                                )
                                .build()
                )
                .header("Prefer", "return=minimal")
                .bodyValue(state)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void saveMove(
            RealtimeMoveDTO moveDTO
    ) {
        try {
            String jsonPayload =
                    objectMapper.writeValueAsString(moveDTO);
            log.info("Supabase move payload: {}",
                    jsonPayload);

            supabaseWebClient.post()
                    .uri("/rest/v1/" +
                            properties
                                    .getTables()
                                    .getRealtimeGameMoves())
                    .header("Prefer", "return=minimal")
                    .bodyValue(moveDTO)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error(
                                                "Supabase move insert error: {}",
                                                errorBody
                                        );
                                        return new RuntimeException(errorBody);
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

        } catch (Exception e) {
            log.error("Failed to save move", e);
            throw new RuntimeException(
                    "Failed to save realtime move"
            );
        }
    }
}
