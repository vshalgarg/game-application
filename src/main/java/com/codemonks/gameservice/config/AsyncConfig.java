package com.codemonks.gameservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "botMoveExecutor")
    public Executor botMoveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);      // 10 threads always alive
        executor.setMaxPoolSize(50);       // max 50 under heavy load
        executor.setQueueCapacity(500);    // queue up to 500 bot moves
        executor.setThreadNamePrefix("bot-move-");
        executor.initialize();
        return executor;
    }
}