package com.codemonks.tambola_engine.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a player within a Tambola room — used both when
 * starting a game (EngineStartGameRequestDTO) and when broadcasting
 * current player state (e.g. inside realtime game-state payloads).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    private Long playerId;

    // Game start hone ke baad, engine isi player ke liye jitne tickets
    // generate karega, unki IDs yahan populate hongi (response me).
    // Setup-request bhejte waqt ye null/empty rahega, response me bharega.
    private List<Long> ticketIds;

    // Is player ne kitne tickets liye hain. ABHI ke liye hardcoded max 4
    // tak limit rahega (GameSetupServiceImpl me validate hoga), lekin field
    // ko yahan isliye rakha hai taaki FUTURE me jab "coin se ticket khareedo"
    // feature aaye, game-service isi field me dynamic count bhej sake -
    // DTO-shape badalne ki zaroorat nahi padegi, sirf validation-limit hategi.
    private Integer ticketCount;
    private Boolean isBot;
    //True once this player has won the FULL_HOUSE (final) rule.
    private Boolean hasWon;
}