package com.codemonks.ludo_engine.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonPropertyOrder({"rows", "columns"})
@Data
public class BoardSize {
    private Integer rows;
    private Integer columns;

}