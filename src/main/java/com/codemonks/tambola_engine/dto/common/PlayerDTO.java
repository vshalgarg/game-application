package com.codemonks.tambola_engine.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    private Long playerId;
    private List<Long> ticketIds;
    private Integer ticketCount;
    private Boolean isBot;
    private Boolean hasWon;
}