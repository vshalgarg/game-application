package com.codemonks.gameservice.dto.request;

import lombok.Data;

@Data
public class MakeMoveRequestDTO {

    private Long userId;
    private int row;
    private int col;
    //private Integer version;
}
