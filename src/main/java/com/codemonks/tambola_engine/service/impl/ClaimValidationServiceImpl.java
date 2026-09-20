package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;
import com.codemonks.tambola_engine.domain.ticket.TicketRow;
import com.codemonks.tambola_engine.service.ClaimValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ClaimValidationServiceImpl implements ClaimValidationService {

    private static final int DEFAULT_EARLY_FIVE_THRESHOLD = 5;

    @Override
    public boolean validate(GameRule rule, TambolaTicket ticket, Set<Integer> calledNumbers) {
        return switch (rule.getRuleType()) {
            case EARLY_FIVE -> validateEarlyFive(rule, ticket, calledNumbers);
            case TOP_LINE -> validateLine(ticket, calledNumbers, 0);
            case MIDDLE_LINE -> validateLine(ticket, calledNumbers, 1);
            case BOTTOM_LINE -> validateLine(ticket, calledNumbers, 2);
            case FULL_HOUSE -> validateFullHouse(ticket, calledNumbers);
        };
    }

    private boolean validateEarlyFive(GameRule rule, TambolaTicket ticket, Set<Integer> calledNumbers) {
        int threshold = (rule.getThreshold() != null) ? rule.getThreshold() : DEFAULT_EARLY_FIVE_THRESHOLD;

        long markedCount = allTicketNumbers(ticket).stream()
                .filter(calledNumbers::contains)
                .count();

        return markedCount >= threshold;
    }
    private boolean validateLine(TambolaTicket ticket, Set<Integer> calledNumbers, int rowIndex) {
        TicketRow targetRow = ticket.getRows().get(rowIndex);
        return targetRow.isFullyMarked(calledNumbers);
    }

    private boolean validateFullHouse(TambolaTicket ticket, Set<Integer> calledNumbers) {
        for (TicketRow row : ticket.getRows()) {
            if (!row.isFullyMarked(calledNumbers)) {
                return false;
            }
        }
        return true;
    }

    private List<Integer> allTicketNumbers(TambolaTicket ticket) {
        return ticket.getRows().stream()
                .flatMap(row -> row.getNumbers().stream())
                .filter(n -> n != null)
                .toList();
    }
}