package com.codemonks.gameservice.engineModule.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@JsonPropertyOrder({"startRow", "startCol", "rows", "cols", "triangles"})
@Data
public class CenterArea {

    private Integer startRow;

    private Integer startCol;

    private Integer rows;

    private Integer cols;

    private List<Triangle> triangles;

}
