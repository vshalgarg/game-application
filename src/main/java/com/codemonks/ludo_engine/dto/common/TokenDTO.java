package com.codemonks.ludo_engine.dto.common;

import com.codemonks.ludo_engine.enums.TokenStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
