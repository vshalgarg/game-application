package com.codemonks.tic_tac_toe_game_engine.service;

import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineMoveRequestDTO;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineStartGameRequestDto;
import com.codemonks.tic_tac_toe_game_engine.dto.response.EngineGameStateResponseDTO;
import com.codemonks.tic_tac_toe_game_engine.enums.GameStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TicTacToeEngineImplTest {

    private TicTacToeEngineImpl engine;

    @BeforeEach
    void setUp() {
        engine = new TicTacToeEngineImpl();
    }

    @Test
    void testStartGame_ShouldInitializeProperly() {
        EngineStartGameRequestDto request = new EngineStartGameRequestDto();
        request.setRoomCode("ROOM123");
        request.setPlayerIds(Arrays.asList(101L, 102L));

        EngineGameStateResponseDTO response = engine.startGame(request);

        assertNotNull(response);
        assertEquals(3, response.getBoardState().size()); // 3 rows
        assertEquals(GameStatusEnum.RUNNING, response.getStatus());
        assertEquals(101L, response.getCurrentTurnUserId()); // Player 1 starts
        assertEquals(2, response.getPlayers().size());
    }

    @Test
    void testMakeMove_ShouldUpdateBoardAndSwitchTurn() {
        // Setup: First start a game
        List<List<String>> board = createEmptyBoard();
        List<PlayerDto> players = Arrays.asList(
                new PlayerDto(101L, 101, "X"),
                new PlayerDto(102L, 102, "O")
        );

        EngineMoveRequestDTO moveRequest = new EngineMoveRequestDTO();
        moveRequest.setGameId(1L);
        moveRequest.setBoardState(board);
        moveRequest.setCurrentTurnUserId(101L);
        moveRequest.setUserId(101L); // Player 1 makes a move
        moveRequest.setPlayers(players);

        Map<String, Object> moveData = new HashMap<>();
        moveData.put("row", 0);
        moveData.put("col", 0);
        moveRequest.setMoveData(moveData);

        // Action
        EngineGameStateResponseDTO response = engine.makeMove(moveRequest);

        // Assert
        assertEquals("X", response.getBoardState().get(0).get(0));
        assertEquals(102L, response.getCurrentTurnUserId()); // Turn switched to Player 2
        assertEquals(GameStatusEnum.RUNNING, response.getStatus());
    }

    @Test
    void testMakeMove_ShouldThrowException_WhenWrongTurn() {
        List<List<String>> board = createEmptyBoard();
        List<PlayerDto> players = Arrays.asList(new PlayerDto(101L, 1, "X"), new PlayerDto(102L, 2, "O"));

        EngineMoveRequestDTO moveRequest = new EngineMoveRequestDTO();
        moveRequest.setCurrentTurnUserId(101L);
        moveRequest.setUserId(102L); // Player 2 tries to move during Player 1's turn
        moveRequest.setPlayers(players);

        Exception exception = assertThrows(RuntimeException.class, () -> engine.makeMove(moveRequest));
        assertTrue(exception.getMessage().contains("Wait for your turn"));
    }

    @Test
    void testCheckWin_Horizontal() {
        List<List<String>> board = createEmptyBoard();
        board.get(0).set(0, "X");
        board.get(0).set(1, "X");
        // We will make the winning move at 0,2

        List<PlayerDto> players = Arrays.asList(new PlayerDto(101L, 1, "X"), new PlayerDto(102L, 2, "O"));

        EngineMoveRequestDTO moveRequest = new EngineMoveRequestDTO();
        moveRequest.setBoardState(board);
        moveRequest.setUserId(101L);
        moveRequest.setCurrentTurnUserId(101L);
        moveRequest.setPlayers(players);

        Map<String, Object> moveData = new HashMap<>();
        moveData.put("row", 0);
        moveData.put("col", 2);
        moveRequest.setMoveData(moveData);

        EngineGameStateResponseDTO response = engine.makeMove(moveRequest);

        assertEquals(GameStatusEnum.WIN, response.getStatus());
        assertEquals(101L, response.getWinnerUserId());
    }

    private List<List<String>> createEmptyBoard() {
        List<List<String>> board = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            board.add(new ArrayList<>(Collections.nCopies(3, null)));
        }
        return board;
    }
}