package com.codemonks.ludo_engine.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonPropertyOrder({"colorIndex", "clip"})
@Data
public class Triangle {

    private Integer colorIndex;
    private String clip;

}
