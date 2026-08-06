package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.service.BoardService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BoardStartupValidator {

    private final BoardService boardService;

    @PostConstruct
    public void validate() {

        log.info("Board Loaded");

        long gridCells = boardService.getBoard().getGrid().stream().mapToLong(Map::size).sum();

        log.info(
                "Grid Cells : {}",
                gridCells
        );

        log.info(
                "Paths : {}",
                boardService.getBoard().getPaths().size()
        );

    }
}