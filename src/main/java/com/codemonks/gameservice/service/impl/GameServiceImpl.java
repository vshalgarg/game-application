package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.dto.response.GameStateResponse;
import com.codemonks.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {


    @Override
    public void startGame(Long roomId) {

    }

    @Override
    public GameStateResponse makeMove(Long roomId, MakeMoveRequestDTO request) {
        return null;
    }
}
