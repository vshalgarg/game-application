
package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.config.SupabaseProperties;
import com.codemonks.tambola_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.tambola_engine.exception.SupabaseStateException;
import com.codemonks.tambola_engine.service.SupabaseRealtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service("tambolaSupabaseRealtimeServiceImpl")
@RequiredArgsConstructor
public class SupabaseRealtimeServiceImpl implements SupabaseRealtimeService {

    private final RestClient supabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public void publishLobbyState(RealtimeLobbyDTO lobbyDTO) {

        String table = properties.getTables().getRealtimeRoomLobby();

        try {
            supabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header(
                            "Prefer",
                            "resolution=merge-duplicates,return=minimal"
                    )
                    .body(lobbyDTO)
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "[LOBBY_STATE_UPSERTED] roomId={} roomCode={} status={} players={}",
                    lobbyDTO.getRoomId(),
                    lobbyDTO.getRoomCode(),
                    lobbyDTO.getRoomStatus(),
                    lobbyDTO.getPlayers() != null
                            ? lobbyDTO.getPlayers().size()
                            : 0
            );

        } catch (Exception e) {

            log.error(
                    "Failed to upsert lobby state. roomId={}",
                    lobbyDTO.getRoomId(),
                    e
            );

            throw new SupabaseStateException(
                    "Failed to upsert lobby state for roomId="
                            + lobbyDTO.getRoomId(),
                    e
            );
        }
    }
}

