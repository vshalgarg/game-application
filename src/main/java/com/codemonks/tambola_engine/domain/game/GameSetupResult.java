package com.codemonks.tambola_engine.domain.game;

import com.codemonks.tambola_engine.dto.common.PlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSetupResult {

    private Long roomId;
    private String status;
    private Integer totalTicketsGenerated;

    // >>> CHANGED: naya field — players (ticketCount, ticketIds, hasWon,
    // isBot) ab is response me bhi aayega, na ki sirf Supabase me.
    private List<PlayerDTO> players;
}