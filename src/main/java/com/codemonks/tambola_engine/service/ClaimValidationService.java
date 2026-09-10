package com.codemonks.tambola_engine.service;


import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.domain.ticket.TambolaTicket;

import java.util.Set;

// Sirf PATTERN-matching ka kaam karta hai - "kya ye ticket is rule ko
// satisfy karta hai, in calledNumbers ke against". Duplicate-claim check,
// ownership-check, etc. ye service ki responsibility NAHI hai (wo
// ClaimService ke andar hoga) - single-responsibility.
public interface ClaimValidationService {

    /**
     * @param rule          jo rule claim ki ja rahi hai (threshold jaisi
     *                      config bhi isi me hoti hai)
     * @param ticket        jis ticket pe claim ki ja rahi hai
     * @param calledNumbers ab tak call hue numbers is room ke
     * @return true agar ticket us rule ko satisfy karta hai
     */
    boolean validate(GameRule rule, TambolaTicket ticket, Set<Integer> calledNumbers);
}