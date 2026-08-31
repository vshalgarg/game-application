package com.codemonks.tambola_engine.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents one Tambola ticket belonging to a player.
 * A player may hold more than one ticket in the same game
 * (TambolaGameState stores these in a Map<playerId, List<TambolaTicket>>,
 * embedded directly for consistency with how the Ludo engine embeds
 * its tokens rather than referencing them by ID).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaTicket {

    private Long ticketId;
    private Long playerId;

    /** The 3 rows that make up this ticket (3 rows x 9 columns, 5 numbers per row). */
    private List<TicketRow> rows;
}