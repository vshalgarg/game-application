package com.codemonks.ludo_engine.dto.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LegalMoveDTO {
    private Long tokenId;
    private Integer dice;
    private boolean exitsBase;
    private boolean landsOnSafeCell;
    private boolean reachesHome;
    private boolean killsOpponent;
    private Long killedTokenId;
    private Long killedPlayerId;
    private Integer resultingPathIndex;
}