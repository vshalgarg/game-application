package com.codemonks.gameservice.engineModule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// >>> NAYA FILE: Tambola-players ka shape Ludo/TicTacToe ke generic
// PlayerDTO se bilkul alag hai (turnOrder/side/consecutiveSixCount
// wahan irrelevant hain) — isliye Tambola ke liye alag, sahi-shaped
// DTO. Ye tambola-engine ke apne PlayerDTO se field-match karta hai.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaPlayerResponseDTO {
    private Long playerId;
    private Integer ticketCount;
    private List<Long> ticketIds;
    private Boolean hasWon;
    private Boolean isBot;
}