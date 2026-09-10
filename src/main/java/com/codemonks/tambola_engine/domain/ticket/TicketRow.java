package com.codemonks.tambola_engine.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Represents a single row of a Tambola ticket.
 * A standard row has 9 columns, of which exactly 5 contain a
 * number (1-90) and 4 are blank (represented as null).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRow {

    /** The 9 cells of this row. A null entry means that column is blank. */
    private List<Integer> numbers;

    /**
     * Convenience check used during claim validation
     * (TOP_LINE/MIDDLE_LINE/BOTTOM_LINE) to see whether every
     * non-blank number in this row has already been called.
     * <p>
     * Kept as a regular method (not a Lombok-generated field)
     * since it's a computed check, not stored state.
     *
     * @param calledNumbers the set of numbers called so far in the game
     * @return true if all non-null numbers in this row are in calledNumbers
     */
    public boolean isFullyMarked(Set<Integer> calledNumbers) {
        return numbers.stream()
                .filter(n -> n != null)
                .allMatch(calledNumbers::contains);
    }
}