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
}