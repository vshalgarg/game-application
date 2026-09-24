package com.codemonks.tambola_engine.repository;

import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;

import java.util.List;

// >>> NOTE: Isme exact-package/imports maine TambolaTicketRepositoryImpl
// ke usage-pattern se reconstruct kiya hai. Agar tumhare original-file
// me koi extra-method/import hai jo maine miss kiya ho, use wapas add
// kar lena — sirf insertAll() ka return-type change hua hai.
public interface TambolaTicketRepository {

    TambolaTicket findById(Long ticketId);

    List<TambolaTicket> findByRoomAndPlayer(Long roomId, Long playerId);

    // >>> CHANGED: void -> List<Long>. Ab insert-hue tickets ke
    // generated ticket_ids wapas milte hain (insertion-order me).
    List<Long> insertAll(String roomCode, List<TambolaTicket> tickets);
}