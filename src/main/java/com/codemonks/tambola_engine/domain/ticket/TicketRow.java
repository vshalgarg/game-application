package com.codemonks.tambola_engine.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRow {

    private List<Integer> numbers;

    public boolean isFullyMarked(Set<Integer> calledNumbers) {
        return numbers.stream()
                .filter(n -> n != null)
                .allMatch(calledNumbers::contains);
    }
}