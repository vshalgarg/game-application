package com.codemonks.tic_tac_toe_game_engine.service.impl;


import com.codemonks.tic_tac_toe_game_engine.config.SupabaseProperties;
import com.codemonks.tic_tac_toe_game_engine.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.tic_tac_toe_game_engine.exception.SupabaseStateException;
import com.codemonks.tic_tac_toe_game_engine.service.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {

    private final RestClient supabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public void publishLobbyState(RealtimeLobbyDTO lobbyDTO) {

        String table = properties.getTables().getRealtimeRoomLobby();

        try {
            supabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "resolution=merge-duplicates,return=minimal")
                    .body(lobbyDTO)
                    .retrieve()
                    .toBodilessEntity();

            log.info("[LOBBY_STATE_UPSERTED] roomId={}", lobbyDTO.getRoomId());

        } catch (Exception e) {
            log.error("Failed to upsert lobby state. roomId={}", lobbyDTO.getRoomId(), e);
            throw new SupabaseStateException(
                    "Failed to upsert lobby state for roomId=" + lobbyDTO.getRoomId(), e
            );
        }
    }
}
