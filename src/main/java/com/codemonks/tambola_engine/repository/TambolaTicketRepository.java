package com.codemonks.tambola_engine.repository;


import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;

import java.util.List;

public interface TambolaTicketRepository {

    TambolaTicket findById(Long ticketId);
    List<TambolaTicket> findByRoomAndPlayer(Long roomId, Long playerId);
    void insertAll(List<TambolaTicket> tickets);
}