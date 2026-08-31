package com.codemonks.tambola_engine.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaTicket {

    private Long ticketId;
    private Long playerId;
    private List<TicketRow> rows;
}