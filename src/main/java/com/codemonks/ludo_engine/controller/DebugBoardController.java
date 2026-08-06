package com.codemonks.ludo_engine.controller;



import com.codemonks.ludo_engine.model.BoardLayout;
import com.codemonks.ludo_engine.service.BoardLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DebugBoardController {

    private final BoardLoader boardLoader;

    @GetMapping("/debug/board")
    public BoardLayout getBoard() {
        return boardLoader.getBoardLayout();
    }
}