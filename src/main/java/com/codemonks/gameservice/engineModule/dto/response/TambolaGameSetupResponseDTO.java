package com.codemonks.gameservice.engineModule.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tambola-engine ke GameSetupResult se exact-match.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaGameSetupResponseDTO {
    private Long roomId;
    private String status;
    private Integer totalTicketsGenerated;
}