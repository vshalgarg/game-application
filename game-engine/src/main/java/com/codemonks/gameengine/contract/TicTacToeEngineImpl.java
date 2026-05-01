package com.codemonks.gameengine.contract;

import com.codemonks.gameengine.core.model.Board;
import com.codemonks.gameengine.core.model.Cell;
import com.codemonks.gameengine.core.model.GameState;
import com.codemonks.gameengine.core.model.Player;
import com.codemonks.gameengine.core.validator.MoveValidationChain;
import com.codemonks.gameengine.core.validator.validators.CellEmptyValidator;
import com.codemonks.gameengine.core.validator.validators.GameOverValidator;
import com.codemonks.gameengine.core.validator.validators.PositionValidator;
import com.codemonks.gameengine.core.validator.validators.TurnValidator;
import com.codemonks.gameengine.dto.requestDto.MoveRequest;
import com.codemonks.gameengine.dto.requestDto.StartGameRequest;
import com.codemonks.gameengine.dto.responseDto.GameStateResponse;
import com.codemonks.gameengine.enums.GameStatusEnum;
import com.codemonks.gameengine.enums.SymbolEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToeEngineImpl implements TicTacToeEngine{

    private final MoveValidationChain validationChain =
            new MoveValidationChain(Arrays.asList(
                    new GameOverValidator(),
                    new TurnValidator(),
                    new PositionValidator(),
                    new CellEmptyValidator()
            ));

    @Override
    public GameStateResponse startGame(StartGameRequest request) {

        List<Player> players = new ArrayList<>();

        players.add(new Player(request.getPlayers().get(0).getPlayerId(), SymbolEnum.X));
        players.add(new Player(request.getPlayers().get(1).getPlayerId(), SymbolEnum.O));

        Board board = new Board(3);

        GameState state = new GameState();
        state.setBoard(board);
        state.setPlayers(players);
        state.setCurrentPlayer(players.get(0));
        state.setStatus(GameStatusEnum.IN_PROGRESS);

        return mapToResponse(state);
    }

    @Override
    public GameStateResponse makeMove(GameStateResponse currentStateDto, MoveRequest moveRequest) {

        GameState state = mapToGameState(currentStateDto);

        Player player = findPlayer(state, moveRequest.getPlayerId());

        int row = moveRequest.getPosition().getRow();
        int col = moveRequest.getPosition().getCol();

        validationChain.validate(state, row, col, player);

        state.getBoard().placeMove(row, col, player.getSymbol());

        if (checkWin(state.getBoard(), player.getSymbol())) {
            state.setStatus(GameStatusEnum.WIN);
            state.setWinner(player);
            return mapToResponse(state);
        }

        if (state.getBoard().isFull()) {
            state.setStatus(GameStatusEnum.DRAW);
            return mapToResponse(state);
        }

        switchTurn(state);

        return mapToResponse(state);
    }

    private Player findPlayer(GameState state, Long playerId) {
        return state.getPlayers()
                .stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
    }

    private void switchTurn(GameState state) {
        List<Player> players = state.getPlayers();
        state.setCurrentPlayer(
                players.get(0).equals(state.getCurrentPlayer()) ? players.get(1) : players.get(0)
        );
    }

    private boolean checkWin(Board board, SymbolEnum symbol) {

        int size = board.getSize();
        // Rows
        for (int i = 0; i < size; i++) {
            boolean win = true;
            for (int j = 0; j < size; j++) {
                if (board.getCell(i, j).getSymbol() != symbol) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }

        // Columns
        for (int j = 0; j < size; j++) {
            boolean win = true;
            for (int i = 0; i < size; i++) {
                if (board.getCell(i, j).getSymbol() != symbol) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }

        // Diagonal
        boolean win = true;
        for (int i = 0; i < size; i++) {
            if (board.getCell(i, i).getSymbol() != symbol) {
                win = false;
                break;
            }
        }
        if (win) return true;

        // Anti-diagonal
        win = true;
        for (int i = 0; i < size; i++) {
            if (board.getCell(i, size - i - 1).getSymbol() != symbol) {
                win = false;
                break;
            }
        }
        return win;
    }

    private GameStateResponse mapToResponse(GameState state) {

        GameStateResponse response = new GameStateResponse();
        List<List<String>> boardDto = new ArrayList<>();

        for (int i = 0; i < state.getBoard().getSize(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < state.getBoard().getSize(); j++) {
                Cell cell = state.getBoard().getCell(i, j);
                if (cell.getSymbol() == null) {
                    row.add(null);
                } else {
                    row.add(cell.getSymbol().name()); // "X" or "O"
                }
            }
            boardDto.add(row);
        }

        response.setBoard(boardDto);
        response.setCurrentPlayer(state.getCurrentPlayer().getPlayerId());
        response.setStatus(GameStatusEnum.valueOf(state.getStatus().name()));

        if (state.getWinner() != null) {
            response.setWinner(state.getWinner().getPlayerId());
        }
        return response;
    }

    private GameState mapToGameState(GameStateResponse dto) {

        if (dto == null || dto.getBoard() == null) {
            throw new IllegalArgumentException("Invalid game state");
        }

        Board board = new Board(dto.getBoard().size());
        for (int i = 0; i < dto.getBoard().size(); i++) {
            for (int j = 0; j < dto.getBoard().size(); j++) {
                String value = dto.getBoard().get(i).get(j);
                if (value != null) {
                    try {
                        board.placeMove(i, j, SymbolEnum.valueOf(value));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid symbol: " + value);
                    }
                }
            }
        }

        // Map players
        List<Player> players = dto.getPlayers()
                .stream()
                .map(p -> new Player(
                        p.getPlayerId(),
                        SymbolEnum.valueOf(p.getSymbol())
                ))
                .toList();

        GameState state = new GameState();
        state.setBoard(board);
        state.setPlayers(players);

        // Current player
        Player current = players.stream()
                .filter(p -> p.getPlayerId().equals(dto.getCurrentPlayer()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid current player"));

        state.setCurrentPlayer(current);
        state.setStatus(GameStatusEnum.valueOf(dto.getStatus().name()));

        // Winner mapping
        if (dto.getWinner() != null) {
            Player winner = players.stream()
                    .filter(p -> p.getPlayerId().equals(dto.getWinner()))
                    .findFirst()
                    .orElse(null);
            state.setWinner(winner);
        }

        // version
        // state.setVersion(dto.getVersion());
        return state;
    }
}
