package com.codemonks.gameservice.service;

import com.codemonks.gameengine.contract.TicTacToeEngine;
import com.codemonks.gameengine.contract.TicTacToeEngineImpl;
import com.codemonks.gameengine.dto.MovePosition;
import com.codemonks.gameengine.dto.PlayerDTO;
import com.codemonks.gameengine.dto.requestDto.MoveRequest;
import com.codemonks.gameengine.dto.requestDto.StartGameRequest;
import com.codemonks.gameengine.dto.responseDto.GameStateResponse;
import com.codemonks.gameservice.dto.request.MakeMoveRequestDTO;
import com.codemonks.gameservice.entity.GameEntity;
import com.codemonks.gameservice.entity.RoomPlayerEntity;
import com.codemonks.gameservice.enums.GameStatusEnum;
import com.codemonks.gameservice.repository.GameEntityRepository;
import com.codemonks.gameservice.repository.RoomPlayerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomPlayerEntityRepository roomPlayerRepository;
    private final GameEntityRepository gameRepository;
    private final SupabaseService supabaseService;

    private final TicTacToeEngine engine = new TicTacToeEngineImpl();

    public void startGame(Long roomId) {

        List<RoomPlayerEntity> players = roomPlayerRepository.findByRoomId(roomId);
        if (players.size() != 2) {
            throw new RuntimeException("TicTacToe requires exactly 2 players");
        }

        // Prepare engine request
        StartGameRequest request = new StartGameRequest();
        request.setPlayers(players.stream()
                .map(p -> {
                    PlayerDTO dto = new PlayerDTO();
                    dto.setPlayerId(p.getUserId());
                    return dto;
                })
                .toList());

        // Call engine
        GameStateResponse state = engine.startGame(request);

        // Save state to Supabase (REALTIME)
        //supabaseService.saveGameState(roomId, state);

        // 5. Create GameEntity (MySQL)
        GameEntity game = new GameEntity();
        game.setRoomId(roomId);
        game.setStatus(GameStatusEnum.IN_PROGRESS);
        game.setCurrentTurn(state.getCurrentPlayer());

        gameRepository.save(game);
    }

    public GameStateResponse makeMove(Long roomId, MakeMoveRequestDTO request) {

        GameStateResponse currentState = supabaseService.getGameState(roomId);

        if (currentState == null) {
            throw new RuntimeException("Game state not found");
        }

//        if (request.getVersion() != null &&
//                currentState.getVersion() != null &&
//                !request.getVersion().equals(currentState.getVersion())) {
//
//            throw new RuntimeException("State mismatch. Please refresh");
//        }

        // Build engine MoveRequest
        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setPlayerId(request.getPlayerId());

        MovePosition position = new MovePosition();
        position.setRow(request.getRow());
        position.setCol(request.getCol());
        moveRequest.setPosition(position);

        GameStateResponse newState;

        try {
            // Call engine
            newState = engine.makeMove(currentState, moveRequest);

        } catch (Exception e) {
            // Convert engine exceptions → API-friendly
            throw new RuntimeException("Invalid move: " + e.getMessage());
        }

        // Increment version
//        if (currentState.getVersion() != null) {
//            newState.setVersion(currentState.getVersion() + 1);
//        } else {
//            newState.setVersion(1);
//        }

        // Save updated state to Supabase (REALTIME TRIGGER)
        supabaseService.saveGameState(roomId, newState);

        // Update DB if game ended
        if (newState.getStatus() == com.codemonks.gameengine.enums.GameStatusEnum.WIN ||
                newState.getStatus() == com.codemonks.gameengine.enums.GameStatusEnum.DRAW) {
            GameEntity game = gameRepository.findByRoomId(roomId)
                    .orElseThrow(() -> new RuntimeException("Game not found"));
            game.setStatus(GameStatusEnum.COMPLETED);
            game.setEndedAt(LocalDateTime.now());

            gameRepository.save(game);
        }
        return newState;
    }
}
