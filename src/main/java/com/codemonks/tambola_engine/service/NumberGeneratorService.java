package com.codemonks.tambola_engine.service;

import java.util.Set;

/**
 * Responsible for deciding "which number comes next", given the
 * numbers already called so far.
 * <p>
 * Pure/stateless — takes calledNumbers as input, returns the next
 * number as output. Does NOT mutate anything and does NOT depend on
 * TambolaGameState, since state is now an immutable per-request
 * snapshot (no in-memory mutation happens anywhere in the engine
 * anymore) — the caller (TimerServiceImpl) is responsible for
 * persisting the returned number via the repository.
 */
public interface NumberGeneratorService {

    /**
     * Picks a number (1-90) that is not present in calledNumbers.
     *
     * @param calledNumbers numbers already called so far in this room
     * @return the next number to call
     * @throws com.codemonks.tambola_engine.exception.BoardExhaustedException
     *         if all 90 numbers have already been called
     */
    Integer generateNextNumber(Set<Integer> calledNumbers);
}