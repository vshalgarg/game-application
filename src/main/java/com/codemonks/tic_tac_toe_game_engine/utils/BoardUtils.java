package com.codemonks.tic_tac_toe_game_engine.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BoardUtils {

        public static List<List<String>> createNewBoard() {
            List<List<String>> board = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                board.add(new ArrayList<>(Collections.nCopies(3, null)));
            }
            return board;
        }

        public static boolean checkWin(List<List<String>> board, String side) {
            for (int i = 0; i < 3; i++) {
                // Rows & Columns
                if (side.equals(board.get(i).get(0)) && side.equals(board.get(i).get(1)) && side.equals(board.get(i).get(2))) return true;
                if (side.equals(board.get(0).get(i)) && side.equals(board.get(1).get(i)) && side.equals(board.get(2).get(i))) return true;
            }
            // Diagonals
            if (side.equals(board.get(0).get(0)) && side.equals(board.get(1).get(1)) && side.equals(board.get(2).get(2))) return true;
            return side.equals(board.get(0).get(2)) && side.equals(board.get(1).get(1)) && side.equals(board.get(2).get(0));
        }

        public static boolean isBoardFull(List<List<String>> board) {
            return board.stream().flatMap(List::stream).noneMatch(Objects::isNull);
        }
    }













