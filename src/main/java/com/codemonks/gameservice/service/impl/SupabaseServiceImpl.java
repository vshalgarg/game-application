package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.config.SupabaseProperties;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.common.RealtimeMoveDTO;
import com.codemonks.gameservice.service.SupabaseService;
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

    @Override
    public void createInitialState(
            RealtimeGameStateDTO state
    ) {

        try{
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
                                        log.error(
                                                "Supabase create state request failed. roomId={}, error={}",
                                                state.getRoomId(),
                                                errorBody
                                        );
                                        return new RuntimeException(errorBody);
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error(
                    "Failed to create realtime game state. roomId={}",
                    state.getRoomId(),
                    e
            );
            throw new RuntimeException("Failed to create realtime game state");
        }
    }

    @Override
    public RealtimeGameStateDTO getGameState(String gameId) {

        try {
            return supabaseWebClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/rest/v1/" +
                                            properties
                                                    .getTables()
                                                    .getRealtimeGameState())
                                    .queryParam("room_id", "eq." + gameId)
                                    .queryParam("select", "*")
                                    .build()
                    )
                    .retrieve()
                    .bodyToFlux(RealtimeGameStateDTO.class)
                    .blockFirst();

        } catch (Exception e) {

            log.error(
                    "Failed to fetch realtime game state. gameId={}",
                    gameId,
                    e
            );

            throw new RuntimeException(
                    "Failed to fetch realtime game state"
            );
        }
    }

    @Override
    public void updateGameState(
            RealtimeGameStateDTO state
    ) {
        try {
        supabaseWebClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/rest/v1/" +
                                        properties
                                                .getTables()
                                                .getRealtimeGameState())
                                .queryParam(
                                        "room_id",
                                        "eq." + state.getRoomId()
                                )
                                .build()
                )
                .header("Prefer", "return=minimal")
                .bodyValue(state)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> {

                                    log.error(
                                            "Supabase update game state failed. roomId={}, error={}",
                                            state.getRoomId(),
                                            errorBody
                                    );

                                    return new RuntimeException(errorBody);
                                })
                )
                .bodyToMono(Void.class)
                .block();
        } catch (Exception e) {

            log.error(
                    "Failed to update realtime game state. roomId={}",
                    state.getRoomId(),
                    e
            );

            throw new RuntimeException(
                    "Failed to update realtime game state"
            );
        }
    }

    @Override
    public void saveMove(
            RealtimeMoveDTO moveDTO
    ) {
        try {

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
                                                "Supabase save move failed. roomId={}, playerId={}, error={}",
                                                moveDTO.getRoomId(),
                                                moveDTO.getPlayerId(),
                                                errorBody
                                        );
                                        return new RuntimeException(errorBody);
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

        } catch (Exception e) {
            log.error(
                    "Failed to save realtime move. roomId={}, playerId={}",
                    moveDTO.getRoomId(),
                    moveDTO.getPlayerId(),
                    e
            );
            throw new RuntimeException(
                    "Failed to save realtime move"
            );
        }
    }
}
