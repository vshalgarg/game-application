package com.codemonks.gameservice.engineModule.model;


import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BoardLayout {
    private Metadata metadata;
    private CenterArea centerArea;
    private List<Grid> grid;
    private Map<Integer, List<Integer>> paths;
}