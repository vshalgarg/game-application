package com.codemonks.gameservice.engineModule.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
 public class RawBoardLayout {
    private Metadata metadata;
    private List<String> colors;
    private CenterArea centerArea;
    private List<Map<String, Grid>> grid;
    private Map<Integer, List<Integer>> paths;
}