package com.codemonks.tambola_engine.domain.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaTicket {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("ticket_id")
    private Long ticketId;

    @JsonProperty("player_id")
    private Long playerId;

    @JsonProperty("ticket_rows")
    private List<TicketRow> rows;
}