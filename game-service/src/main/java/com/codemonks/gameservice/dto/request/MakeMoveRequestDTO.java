package com.codemonks.gameservice.dto.request;

import lombok.Data;

@Data
public class MakeMoveRequestDTO {

    private Long playerId;
    private int row;
    private int col;
    //private Integer version;
}
