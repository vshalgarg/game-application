package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.exception.BoardExhaustedException;
import com.codemonks.tambola_engine.service.NumberGeneratorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Default implementation of NumberGeneratorService.
 * Pure/stateless singleton — holds no per-room data, does not mutate
 * its input. TimerServiceImpl persists the returned number itself.
 */
@Service
public class NumberGeneratorServiceImpl implements NumberGeneratorService {

    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 90;

    private final Random random = new Random();

    @Override
    public Integer generateNextNumber(Set<Integer> calledNumbers) {

        if (calledNumbers.size() >= MAX_NUMBER) {
            throw new BoardExhaustedException();
        }

        // O(90) cost is negligible — runs once per timer tick, not a hot path.
        List<Integer> remaining = new ArrayList<>();
        for (int number = MIN_NUMBER; number <= MAX_NUMBER; number++) {
            if (!calledNumbers.contains(number)) {
                remaining.add(number);
            }
        }

        return remaining.get(random.nextInt(remaining.size()));
    }
}