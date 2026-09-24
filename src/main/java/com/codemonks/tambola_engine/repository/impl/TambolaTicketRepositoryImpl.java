package com.codemonks.tambola_engine.repository.impl;

import com.codemonks.tambola_engine.config.SupabaseProperties;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.domain.ticket.TicketRow;
import com.codemonks.tambola_engine.exception.SupabaseStateException;
import com.codemonks.tambola_engine.repository.TambolaTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Repository("tambolaTicketRepositoryImpl")
@RequiredArgsConstructor
public class TambolaTicketRepositoryImpl implements TambolaTicketRepository {

    private final RestClient tambolaSupabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public TambolaTicket findById(Long ticketId) {
        String table = properties.getTables().getTambolaTickets();
        try {
            List<TambolaTicket> result = tambolaSupabaseRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("ticket_id", "eq." + ticketId)
                            .queryParam("select", "*")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TambolaTicket>>() {});

            return (result == null || result.isEmpty()) ? null : result.get(0);

        } catch (Exception e) {
            log.error("Failed to fetch ticket. ticketId={}", ticketId, e);
            throw new SupabaseStateException("Failed to fetch ticket " + ticketId, e);
        }
    }

    @Override
    public List<TambolaTicket> findByRoomAndPlayer(Long roomId, Long playerId) {
        String table = properties.getTables().getTambolaTickets();
        try {
            List<TambolaTicket> result = tambolaSupabaseRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("room_id", "eq." + roomId)
                            .queryParam("player_id", "eq." + playerId)
                            .queryParam("select", "*")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TambolaTicket>>() {});

            return result == null ? List.of() : result;

        } catch (Exception e) {
            log.error("Failed to fetch tickets. roomId={} playerId={}", roomId, playerId, e);
            throw new SupabaseStateException("Failed to fetch tickets for roomId=" + roomId, e);
        }
    }

    // >>> CHANGED: void -> List<Long>. Pehle "Prefer: return=minimal" tha,
    // isliye generated ticket_id (IDENTITY column) kabhi wapas nahi aata
    // tha — GameSetupServiceImpl me playerDTO.setTicketIds() ke liye
    // hamesha-null-hi-milta-tha. Ab "return=representation" use karke
    // insert-hue rows wapas mangate hain, aur unke ticket_id nikaal ke
    // return karte hain, INSERTION-ORDER me (jo caller ke newTickets-list
    // ke order se match karta hai).
    @Override
    public List<Long> insertAll(String roomCode, List<TambolaTicket> tickets) {
        if (tickets.isEmpty()) return List.of();
        String table = properties.getTables().getTambolaTickets();
        try {

            List<InsertRow> body = tickets.stream()
                    .map(t -> new InsertRow(t.getRoomId(), roomCode, t.getPlayerId(), t.getRows()))
                    .toList();

            List<InsertedRowResult> inserted = tambolaSupabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=representation")   // >>> CHANGED
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<InsertedRowResult>>() {});   // >>> CHANGED

            if (inserted == null) {
                log.warn("[TAMBOLA_TICKETS_INSERT_EMPTY_RESPONSE] roomCode={} count={}", roomCode, tickets.size());
                return List.of();
            }

            return inserted.stream()
                    .map(InsertedRowResult::ticket_id)
                    .toList();

        } catch (Exception e) {
            log.error("Failed to insert tickets", e);
            throw new SupabaseStateException("Failed to insert tickets", e);
        }
    }

    private record InsertRow(Long room_id, String room_code, Long player_id, List<TicketRow> ticket_rows) {}

    // >>> NAYA: PostgREST "return=representation" response ko map karne
    // ke liye — sirf ticket_id nikaalna hai, baaki fields ignore.
    private record InsertedRowResult(Long ticket_id) {}
}