package com.codemonks.gameservice.engineModule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"metadata", "grid", "paths", "baseCells", "centerArea"})
@Data
public class BoardLayout {
    private Metadata metadata;
    private List<Map<Integer, Grid>> grid;
    private Map<Integer, List<Integer>> paths;
    private Map<Integer, List<Integer>> baseCells;
    private CenterArea centerArea;

    @JsonIgnore
    private Map<Integer, Grid> gridMap;
}
