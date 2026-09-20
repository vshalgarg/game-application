package com.codemonks.tambola_engine.util;



import lombok.extern.slf4j.Slf4j;
import java.util.function.Supplier;

@Slf4j
public final class OptimisticRetry {

    private static final int DEFAULT_MAX_ATTEMPTS = 5;

    private OptimisticRetry() {}

    public static boolean attempt(Supplier<Boolean> attemptFn) {
        return attempt(attemptFn, DEFAULT_MAX_ATTEMPTS);
    }

    public static boolean attempt(Supplier<Boolean> attemptFn, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            if (Boolean.TRUE.equals(attemptFn.get())) {
                return true;
            }
            log.debug("Optimistic-write attempt {} failed (version mismatch) - retrying", i + 1);
        }
        log.warn("Optimistic-write exhausted {} attempts without success", maxAttempts);
        return false;
    }
}