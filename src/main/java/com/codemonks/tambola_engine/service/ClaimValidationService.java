package com.codemonks.tambola_engine.service;


import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;

import java.util.Set;

public interface ClaimValidationService {
    boolean validate(GameRule rule, TambolaTicket ticket, Set<Integer> calledNumbers);
}