package com.codemonks.tambola_engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Provides the shared TaskScheduler bean used by TimerService to run
 * per-room, dynamic-interval number-calling jobs.
 * <p>
 * Exposed as the TaskScheduler interface (not the concrete
 * ThreadPoolTaskScheduler type), consistent with the Ludo engine's
 * SchedulingConfig - callers depend on the abstraction, so the
 * underlying implementation can change later without touching
 * every class that injects it.
 * <p>
 * A single scheduler is shared across all rooms - each room's job
 * is just one scheduled task within this shared pool, tracked
 * individually via its own ScheduledFuture (see TimerService).
 */
@Configuration
public class SchedulerConfig {

    /**
     * Pool size chosen to comfortably handle many concurrent rooms
     * ticking independently; each tick task is short-lived (one
     * number generation + state update), so threads free up quickly.
     * Tune based on expected concurrent-room load.
     */
    private static final int POOL_SIZE = 10;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        scheduler.setThreadNamePrefix("tambola-delay-");
        scheduler.initialize();
        return scheduler;
    }
}