package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.config.SupabaseProperties;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeGameStateDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeMoveDTO;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseServiceImpl implements SupabaseService {

    private final WebClient supabaseWebClient;

    private final SupabaseProperties properties;

    @Override
    public void upsertLobbyState(
            RealtimeLobbyDTO lobbyDTO
    ) {

        String tableName =
                properties.getTables().getRealtimeRoomLobby();

        try {

            supabaseWebClient.post()
                    .uri("/rest/v1/" + tableName)
                    .header(
                            "Prefer",
                            "resolution=merge-duplicates,return=minimal"
                    )
                    .bodyValue(lobbyDTO)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            response -> response.bodyToMono(String.class)
                                    .map(errorBody -> {

                                        log.error(
                                                """
                                                Supabase lobby update failed.
                                                
                                                Table={}
                                                Status={}
                                                Payload={}
                                                Error={}
                                                """,
                                                tableName,
                                                response.statusCode(),
                                                lobbyDTO,
                                                errorBody
                                        );

                                        return new RuntimeException(errorBody);
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

            log.info(
                    "Lobby state updated successfully. roomId={}",
                    lobbyDTO.getRoomId()
            );

        } catch (Exception e) {

            log.error(
                    "Unexpected error while updating lobby state. roomId={}",
                    lobbyDTO.getRoomId(),
                    e
            );

            throw new GameException(
                    REALTIME_STATE_UPDATE_FAILED
            );
        }
    }

    @Override
    public void upsertGameState(
            RealtimeGameStateDTO state
    ) {

        String tableName = properties.getTables().getRealtimeGameState();

        try {
            supabaseWebClient.post()
                    .uri("/rest/v1/" + tableName)
                    .header(
                            "Prefer",
                            "resolution=merge-duplicates,return=minimal"
                    )
                    .bodyValue(state)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            response -> response.bodyToMono(String.class)
                                    .map(errorBody -> {

                                        log.error(
                                                """
                                                Supabase game state update failed.
                                                
                                                Table={}
                                                Status={}
                                                Payload={}
                                                Error={}
                                                """,
                                                tableName,
                                                response.statusCode(),
                                                state,
                                                errorBody
                                        );

                                        return new RuntimeException(errorBody);
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

            log.info(
                    "Game state updated successfully. roomId={}",
                    state.getRoomId()
            );

        } catch (Exception e) {

            log.error(
                    "Unexpected error while updating game state. roomId={}",
                    state.getRoomId(),
                    e
            );

            throw new GameException(
                    REALTIME_STATE_UPDATE_FAILED
            );
        }
    }

    @Override
    public RealtimeGameStateDTO getGameState(
            Long roomId
    ) {

        String tableName =
                properties.getTables().getRealtimeGameState();

        try {

            RealtimeGameStateDTO state =
                    supabaseWebClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/rest/v1/" + tableName)
                                    .queryParam("room_id", "eq." + roomId)
                                    .queryParam("select", "*")
                                    .build()
                            )
                            .retrieve()
                            .onStatus(
                                    HttpStatusCode::isError,
                                    response -> response.bodyToMono(String.class)
                                            .map(errorBody -> {

                                                log.error(
                                                        """
                                                        Supabase fetch game state failed.
                                                        
                                                        Table={}
                                                        RoomId={}
                                                        Status={}
                                                        Error={}
                                                        """,
                                                        tableName,
                                                        roomId,
                                                        response.statusCode(),
                                                        errorBody
                                                );

                                                return new RuntimeException(errorBody);
                                            })
                            )
                            .bodyToFlux(RealtimeGameStateDTO.class)
                            .blockFirst();

            log.info(
                    "Game state fetched successfully. roomId={}",
                    roomId
            );

            return state;

        } catch (Exception e) {

            log.error(
                    "Unexpected error while fetching game state. roomId={}",
                    roomId,
                    e
            );

            throw new GameException(
                    REALTIME_STATE_FETCH_FAILED
            );
        }
    }

    @Override
    public void saveMove(
            RealtimeMoveDTO moveDTO
    ) {

        String tableName =
                properties.getTables().getRealtimeGameMoves();

        try {

            supabaseWebClient.post()
                    .uri("/rest/v1/" + tableName)
                    .header("Prefer", "return=minimal")
                    .bodyValue(moveDTO)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            response -> response.bodyToMono(String.class)
                                    .map(errorBody -> {

                                        log.error(
                                                """
                                                Supabase move save failed.
                                                
                                                Table={}
                                                Status={}
                                                Payload={}
                                                Error={}
                                                """,
                                                tableName,
                                                response.statusCode(),
                                                moveDTO,
                                                errorBody
                                        );

                                        return new RuntimeException(errorBody);
                                    })
                    )
                    .bodyToMono(Void.class)
                    .block();

            log.info(
                    "Move saved successfully. roomId={}",
                    moveDTO.getRoomId()
            );

        } catch (Exception e) {

            log.error(
                    "Unexpected error while saving move. roomId={}",
                    moveDTO.getRoomId(),
                    e
            );

            throw new GameException(
                    REALTIME_MOVE_SAVE_FAILED
            );
        }
    }
}