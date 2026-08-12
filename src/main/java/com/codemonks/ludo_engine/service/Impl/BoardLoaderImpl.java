package com.codemonks.ludo_engine.service.Impl;
import com.codemonks.ludo_engine.config.BoardProperties;
import com.codemonks.ludo_engine.model.BoardLayout;
import com.codemonks.ludo_engine.model.Grid;
import com.codemonks.ludo_engine.service.BoardLoader;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class BoardLoaderImpl implements BoardLoader {

    private final ObjectMapper objectMapper;
    private final BoardProperties boardProperties;

    @Getter
    private BoardLayout boardLayout;

    @PostConstruct
    public void loadBoard() {
        try {
            ClassPathResource resource = new ClassPathResource(boardProperties.getLayoutFile());

            // Step 1: parse the JSON directly into the BoardLayout DTO
            boardLayout = objectMapper.readValue(resource.getInputStream(), BoardLayout.class);

            // Step 2: build the id -> cell lookup used by services
            Map<Integer, Grid> gridMap = boardLayout.getGrid().stream()
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));

            // Step 3: derive from the start ("S") cells of each base
            Map<Integer, List<Integer>> baseCells = gridMap.entrySet().stream()
                    .filter(entry -> "S".equals(entry.getValue().getType())
                            && entry.getValue().getTokenColorIndex() != null)
                    .collect(Collectors.groupingBy(
                            entry -> entry.getValue().getTokenColorIndex(),
                            LinkedHashMap::new,
                            Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                    ));
            baseCells.values().forEach(Collections::sort);

            boardLayout.setGridMap(gridMap);
            boardLayout.setBaseCells(baseCells);

            log.info(
                    "[BOARD_LOADED] File:{} Grid:{} Paths:{}",
                    boardProperties.getLayoutFile(),
                    boardLayout.getGrid().size(),
                    boardLayout.getPaths().size()
            );
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load board-layout.json", ex);
        }
    }

    @Override
    public BoardLayout getBoardLayout() {
        log.info("[BOARD_FROM_MEMORY] Hash:{}", System.identityHashCode(boardLayout));
        return boardLayout;
    }
}
