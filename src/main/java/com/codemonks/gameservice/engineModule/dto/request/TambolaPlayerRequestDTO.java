package com.codemonks.gameservice.engineModule.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tambola-engine ke PlayerDTO se field-naam-ba-field match karta hai.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TambolaPlayerRequestDTO {
    private Long playerId;
    private Integer ticketCount;
    private Boolean isBot;
}
