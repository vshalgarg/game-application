package com.codemonks.ludo_engine.service.Impl;
import com.codemonks.ludo_engine.config.BoardProperties;
import com.codemonks.ludo_engine.model.BoardLayout;
import com.codemonks.ludo_engine.model.Grid;
import com.codemonks.ludo_engine.model.RawBoardLayout;
import com.codemonks.ludo_engine.service.BoardLoader;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Comparator;
import java.util.List;
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

            // Step 1: parse the JSON
            RawBoardLayout raw = objectMapper.readValue(resource.getInputStream(), RawBoardLayout.class);

            // Step 2: flatten grid
            List<Grid> flatGrid = raw.getGrid().stream()
                    .flatMap(map -> map.entrySet().stream())
                    .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
                    .map(entry -> {
                        Grid cell = entry.getValue();
                        cell.setId(Integer.parseInt(entry.getKey()));
                        return cell;
                    })
                    .collect(Collectors.toList());

            // Step 3: build the clean BoardLayout your services use
            boardLayout = new BoardLayout();
            boardLayout.setMetadata(raw.getMetadata());
            boardLayout.setCenterArea(raw.getCenterArea());
            boardLayout.setGrid(flatGrid);
            boardLayout.setPaths(raw.getPaths());

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
