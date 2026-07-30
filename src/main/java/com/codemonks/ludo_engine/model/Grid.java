package com.codemonks.ludo_engine.model;

import lombok.Data;

@Data
public class Grid {

    private Integer id;
    private Integer row;

    private Integer col;

    private String type;

    private Integer colorIndex;
    private Integer tokenColorIndex;
    private String arrowDirection;
    private String arrowColorIndex;

}