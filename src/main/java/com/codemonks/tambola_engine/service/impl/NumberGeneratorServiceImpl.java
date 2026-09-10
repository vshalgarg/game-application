package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.exception.BoardExhaustedException;
import com.codemonks.tambola_engine.service.NumberGeneratorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Default implementation of NumberGeneratorService.
 * <p>
 * This is a Spring-managed singleton bean (@Service) - it holds no
 * per-room state itself. All per-room state lives in the
 * TambolaGameState instance passed into each call, which is why
 * this class is safe to share across every room in the application.
 */
@Service
public class NumberGeneratorServiceImpl implements NumberGeneratorService {

    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 90;

    private final Random random = new Random();

    @Override
    public Integer generateNextNumber(TambolaGameState state) {
        Set<Integer> calledNumbers = state.getCalledNumbers();

        if (calledNumbers.size() >= MAX_NUMBER) {
            throw new BoardExhaustedException(state.getRoomId());
        }

        // Build the pool of numbers not yet called. Recomputed on every
        // call rather than cached separately, so there is only one
        // source of truth (calledNumbers) and no risk of a second,
        // separately-maintained pool drifting out of sync with it.
        // O(90) cost is negligible here since this runs once per timer
        // tick (every few seconds), not on a hot path.
        List<Integer> remaining = new ArrayList<>();
        for (int number = MIN_NUMBER; number <= MAX_NUMBER; number++) {
            if (!calledNumbers.contains(number)) {
                remaining.add(number);
            }
        }

        int picked = remaining.get(random.nextInt(remaining.size()));

        // callNumber() is the single synchronized guard against duplicate
        // recording. It only returns null if `picked` was already in
        // calledNumbers - which should not happen here since we just
        // built `remaining` from the same set - but the retry stays as
        // a defensive fallback in case of an unexpected race.
        Integer recorded = state.callNumber(picked);
        if (recorded == null) {
            return generateNextNumber(state);
        }

        return recorded;
    }
}