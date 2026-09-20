package com.codemonks.tambola_engine.util;


import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.domain.ticket.TicketRow;
import com.codemonks.tambola_engine.exception.TicketPersistenceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TicketGenerator {

    private static final int TOTAL_ROWS = 3;
    private static final int TOTAL_COLUMNS = 9;
    private static final int NUMBERS_PER_ROW = 5;
    private static final int TOTAL_FILLED_CELLS = TOTAL_ROWS * NUMBERS_PER_ROW;
    private static final int MAX_NUMBERS_PER_COLUMN = 3;

    public TambolaTicket generateTicket(Long playerId) {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int[] columnCounts = generateColumnCounts(random);
        List<List<Integer>> rowsPerColumn = assignRowsToColumns(columnCounts, random);
        Integer[][] grid = new Integer[TOTAL_ROWS][TOTAL_COLUMNS];

        for (int column = 0; column < TOTAL_COLUMNS; column++) {
            int countForThisColumn = columnCounts[column];
            if (countForThisColumn == 0) {
                continue;
            }

            List<Integer> pickedNumbers = pickRandomNumbersForColumn(column, countForThisColumn, random);

            Collections.sort(pickedNumbers);
            List<Integer> rowsForThisColumn = rowsPerColumn.get(column);
            for (int i = 0; i < countForThisColumn; i++) {
                int targetRow = rowsForThisColumn.get(i);
                grid[targetRow][column] = pickedNumbers.get(i);
            }
        }
        List<TicketRow> ticketRows = new ArrayList<>();
        for (int row = 0; row < TOTAL_ROWS; row++) {
            ticketRows.add(new TicketRow(Arrays.asList(grid[row])));
        }

        return new TambolaTicket(null, null, playerId, ticketRows);
    }

    private int[] generateColumnCounts(ThreadLocalRandom random) {
        int[] counts = new int[TOTAL_COLUMNS];

        Arrays.fill(counts, 1);

        int remaining = TOTAL_FILLED_CELLS - TOTAL_COLUMNS;
        while (remaining > 0) {
            int randomColumn = random.nextInt(TOTAL_COLUMNS);
            if (counts[randomColumn] < MAX_NUMBERS_PER_COLUMN) {
                counts[randomColumn]++;
                remaining--;
            }
        }

        return counts;
    }

    private List<List<Integer>> assignRowsToColumns(int[] columnCounts, ThreadLocalRandom random) {
        int maxAttempts = 200;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int[] rowRemainingCapacity = {NUMBERS_PER_ROW, NUMBERS_PER_ROW, NUMBERS_PER_ROW};

            List<List<Integer>> assignment = new ArrayList<>();
            for (int c = 0; c < TOTAL_COLUMNS; c++) {
                assignment.add(new ArrayList<>());
            }
            List<Integer> columnOrder = new ArrayList<>();
            for (int c = 0; c < TOTAL_COLUMNS; c++) columnOrder.add(c);
            Collections.shuffle(columnOrder, random);

            boolean attemptFailed = false;

            for (int column : columnOrder) {
                int needed = columnCounts[column];

                List<Integer> availableRows = new ArrayList<>();
                for (int row = 0; row < TOTAL_ROWS; row++) {
                    if (rowRemainingCapacity[row] > 0) {
                        availableRows.add(row);
                    }
                }

                if (availableRows.size() < needed) {
                    attemptFailed = true;
                    break;
                }

                Collections.shuffle(availableRows, random);
                List<Integer> chosenRows = availableRows.subList(0, needed);

                for (int row : chosenRows) {
                    assignment.get(column).add(row);
                    rowRemainingCapacity[row]--;
                }
            }

            if (!attemptFailed
                    && rowRemainingCapacity[0] == 0
                    && rowRemainingCapacity[1] == 0
                    && rowRemainingCapacity[2] == 0) {
                return assignment;
            }
        }

        throw new TicketPersistenceException(
                "Ticket layout generate nahi ho paya after " + maxAttempts + " attempts"
        );

    }

    private List<Integer> pickRandomNumbersForColumn(int column, int count, ThreadLocalRandom random) {
        int rangeStart;
        int rangeEnd;

        if (column == 0) {
            rangeStart = 1;
            rangeEnd = 9;
        } else if (column == TOTAL_COLUMNS - 1) {
            rangeStart = 80;
            rangeEnd = 90;
        } else {
            rangeStart = column * 10;
            rangeEnd = column * 10 + 9;
        }

        List<Integer> allNumbersInRange = new ArrayList<>();
        for (int n = rangeStart; n <= rangeEnd; n++) {
            allNumbersInRange.add(n);
        }
        Collections.shuffle(allNumbersInRange, random);

        return new ArrayList<>(allNumbersInRange.subList(0, count));
    }
}