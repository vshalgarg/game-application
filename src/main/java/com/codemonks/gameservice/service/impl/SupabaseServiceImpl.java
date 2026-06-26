package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.config.SupabaseProperties;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
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
}