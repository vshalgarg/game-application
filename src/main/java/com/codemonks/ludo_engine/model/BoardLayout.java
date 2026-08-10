package com.codemonks.ludo_engine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"metadata", "grid", "paths", "centerArea"})
@Data
public class BoardLayout {
    private Metadata metadata;
    private List<Map<Integer, Grid>> grid;
    private Map<Integer, List<Integer>> paths;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Map<Integer, List<Integer>> baseCells;
    private CenterArea centerArea;

    @JsonIgnore
    private Map<Integer, Grid> gridMap;
}
