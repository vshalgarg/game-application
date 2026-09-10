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

    // EARLY_FIVE ka default threshold - agar rule.getThreshold() null hai
    // (host ne configure nahi kiya), to yahi fallback use hoga. Isi wajah
    // se GameRule me threshold field rakha tha - kal agar koi room isko
    // 7 chahiye, sirf rule.setThreshold(7) hoga, ye class touch nahi hogi.
    private static final int DEFAULT_EARLY_FIVE_THRESHOLD = 5;

    @Override
    public boolean validate(GameRule rule, TambolaTicket ticket, Set<Integer> calledNumbers) {
        // Har rule-type ka apna alag pattern-check hai - switch se route
        // karte hain sahi private-method pe.
        return switch (rule.getRuleType()) {
            case EARLY_FIVE -> validateEarlyFive(rule, ticket, calledNumbers);
            case TOP_LINE -> validateLine(ticket, calledNumbers, 0);
            case MIDDLE_LINE -> validateLine(ticket, calledNumbers, 1);
            case BOTTOM_LINE -> validateLine(ticket, calledNumbers, 2);
            case FULL_HOUSE -> validateFullHouse(ticket, calledNumbers);
        };
    }

    // Early Five: ticket ke total non-blank numbers me se kam-se-kam
    // `threshold` (default 5) already call ho chuke hone chahiye - chahe
    // wo kisi bhi row se ho, koi row-specific restriction nahi.
    private boolean validateEarlyFive(GameRule rule, TambolaTicket ticket, Set<Integer> calledNumbers) {
        int threshold = (rule.getThreshold() != null) ? rule.getThreshold() : DEFAULT_EARLY_FIVE_THRESHOLD;

        long markedCount = allTicketNumbers(ticket).stream()
                .filter(calledNumbers::contains)
                .count();

        return markedCount >= threshold;
    }

    // Top/Middle/Bottom Line: us specific row (rowIndex 0/1/2) ke saare
    // non-blank numbers already call ho chuke hone chahiye. TicketRow me
    // isFullyMarked() method already hai isi check ke liye.
    private boolean validateLine(TambolaTicket ticket, Set<Integer> calledNumbers, int rowIndex) {
        TicketRow targetRow = ticket.getRows().get(rowIndex);
        return targetRow.isFullyMarked(calledNumbers);
    }

    // Full House: ticket ke SAARE (teeno rows ke) non-blank numbers
    // already call ho chuke hone chahiye - sabse strict rule, typically
    // game ka last/final rule.
    private boolean validateFullHouse(TambolaTicket ticket, Set<Integer> calledNumbers) {
        for (TicketRow row : ticket.getRows()) {
            if (!row.isFullyMarked(calledNumbers)) {
                return false; // koi ek bhi row incomplete hai to Full House nahi bana
            }
        }
        return true;
    }

    // Ticket ke teeno rows ke saare non-null numbers ek flat list me
    // - Early Five jaisi cross-row checks ke liye helper.
    private List<Integer> allTicketNumbers(TambolaTicket ticket) {
        return ticket.getRows().stream()
                .flatMap(row -> row.getNumbers().stream())
                .filter(n -> n != null)
                .toList();
    }
}