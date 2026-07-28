package com.codemonks.gameservice.engineModule.model;

import lombok.Data;

import java.util.List;

@Data
public class CenterArea {

    private Integer startRow;

    private Integer startCol;

    private Integer rows;

    private Integer cols;

    private List<Triangle> triangles;

}
