package com.codemonks.ludo_engine.model;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;
import java.util.Map;

@Data
public class BoardLayout {
    private Metadata metadata;
    private List<String> colors;
    private CenterArea centerArea;
    private List<Grid> grid;
    @JsonIgnore
    private Map<Integer, Grid> gridMap;

    private Map<Integer, List<Integer>> paths;
    private Map<Integer, List<Integer>> baseCells;
}