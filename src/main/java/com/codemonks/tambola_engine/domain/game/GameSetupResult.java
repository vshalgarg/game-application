package com.codemonks.tambola_engine.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// GameSetupService.initializeGame() ka return type.
// Isme do cheezein hain:
// 1) Ye batana ki setup safal hua aur room ab kis roomId/status pe hai
//    (caller - jaise TambolaEngineImpl - isko response me game-service
//    ko wapas bhej sakta hai).
// 2) Generate hue saare tickets, RealtimeTicketDTO ke roop me - taaki
//    caller inhe seedha Supabase ki realtime_tambola_tickets table me
//    persist kar sake (isi wajah se yahan RealtimeTicketDTO use kiya,
//    domain TambolaTicket nahi - ye result "bahar bhejne" ke liye hai).
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSetupResult {

    private Long roomId;

    // Setup ke turant baad status hamesha INITIALIZED hoga - explicit rakha
    // hai taaki caller ko dobara TambolaGameState fetch na karni pade sirf
    // status confirm karne ke liye.
    private String status;

    // Kitne total tickets generate hue is room ke saare players
    // milaakar - caller/frontend ko simple summary ke liye kaafi hai,
    // poori ticket-data ki zaroorat nahi (wo already Supabase me
    // persist ho chuki, frontend Supabase-realtime se hi live data
    // lega).
    private Integer totalTicketsGenerated;
}