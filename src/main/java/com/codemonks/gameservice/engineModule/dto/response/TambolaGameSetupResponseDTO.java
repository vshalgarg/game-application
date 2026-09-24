package com.codemonks.gameservice.engineModule.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Tambola-engine ke GameSetupResult se exact-match.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaGameSetupResponseDTO {
    private Long roomId;
    private String status;
    private Integer totalTicketsGenerated;

    // >>> CHANGED: naya field — tambola-engine ke GameSetupResult.players
    // se match karta hai, isliye Feign-deserialization bina kisi extra
    // mapping-code ke kaam karega.
    private List<TambolaPlayerResponseDTO> players;
}