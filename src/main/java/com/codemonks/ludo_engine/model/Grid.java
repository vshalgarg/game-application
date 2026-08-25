package com.codemonks.ludo_engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonPropertyOrder({"row", "col", "type", "colorIndex"})
@Data
public class Grid {
    private Integer row;
    private Integer col;
    private String type;
    private Integer colorIndex;
    private Integer tokenColorIndex;
    private String arrowDirection;
    private String arrowColorIndex;

}
