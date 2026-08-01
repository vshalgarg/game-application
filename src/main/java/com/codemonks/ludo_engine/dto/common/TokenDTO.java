package com.codemonks.ludo_engine.dto.common;

import com.codemonks.ludo_engine.enums.TokenStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {

    private Long tokenId;
    private TokenStateEnum state;
    private Integer pathIndex;
    private Integer pathId;
    private Integer baseSlot;
    private Integer baseSlotId;
    private Boolean tokenKilled;
    private List<Integer> forwardJourney;
    private List<Integer> backwardJourney;
}
