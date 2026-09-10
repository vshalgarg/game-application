package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.game.GameStateRegistry;
import com.codemonks.tambola_engine.domain.game.TambolaGameState;
import com.codemonks.tambola_engine.enums.GameStatusEnum;
import com.codemonks.tambola_engine.exception.BoardExhaustedException;
import com.codemonks.tambola_engine.service.NumberGeneratorService;
import com.codemonks.tambola_engine.service.SupabaseRealtimeService;
import com.codemonks.tambola_engine.service.TimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimerServiceImpl implements TimerService {

    private final TaskScheduler taskScheduler;
    private final NumberGeneratorService numberGeneratorService;
    private final GameStateRegistry gameStateRegistry;

    // NAYA: har tick ke baad naya number bhi Supabase me turant bhejna
    // hai - warna frontend ka realtime-subscription kabhi trigger hi
    // nahi hoga jab tak koi claim na ho (jo bahut der tak nahi bhi ho
    // sakta - players number-calling dekhte rehte hain claim se pehle).
    private final SupabaseRealtimeService supabaseRealtimeService;

    private final Map<Long, ScheduledFuture<?>> roomTimers = new ConcurrentHashMap<>();

    @Override
    public void startTimer(Long roomId) {
        TambolaGameState state = gameStateRegistry.get(roomId);

        if (roomTimers.containsKey(roomId)) {
            log.warn("Timer already running for room {}, ignoring duplicate start", roomId);
            return;
        }
        Duration interval = Duration.ofSeconds(state.getTimerIntervalSeconds());
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                () -> tick(roomId),
                Instant.now().plus(interval),
                interval
        );

        roomTimers.put(roomId, future);
    }

    @Override
    public void stopTimer(Long roomId) {
        ScheduledFuture<?> future = roomTimers.remove(roomId);
        if (future != null) {
            future.cancel(false);
        }
    }

    @Override
    public void scheduleResume(Long roomId, int delaySeconds) {
        stopTimer(roomId);
        taskScheduler.schedule(
                () -> resumeAfterWin(roomId),
                Instant.now().plusSeconds(delaySeconds)
        );
    }

    private void tick(Long roomId) {
        TambolaGameState state = gameStateRegistry.get(roomId);

        if (state.getStatus() != GameStatusEnum.RUNNING) {
            return;
        }
        try {
            numberGeneratorService.generateNextNumber(state);

            // NAYA: number successfully call hote hi turant Supabase
            // update - taaki frontend har number turant realtime dekh
            // sake, sirf claim-events pe hi update na mile.
            supabaseRealtimeService.upsertGameState(state);

        } catch (BoardExhaustedException e) {
            log.info("Board exhausted for room {}, stopping timer", roomId);
            stopTimer(roomId);

            // Board khatam ho gaya bina FULL_HOUSE claim hue - ye ek
            // edge-case hai (game khinch gaya, koi jeeta hi nahi last
            // rule pe). State ko FINISHED mark karke persist kar dena
            // sahi hai, taaki room permanently "RUNNING" na dikhta rahe
            // Supabase me.
            state.setStatus(GameStatusEnum.FINISHED);
            supabaseRealtimeService.upsertGameState(state);
            gameStateRegistry.remove(roomId);
        }
    }

    private void resumeAfterWin(Long roomId) {
        TambolaGameState state = gameStateRegistry.get(roomId);

        if (state.getStatus() == GameStatusEnum.FINISHED) {
            return;
        }
        state.setStatus(GameStatusEnum.RUNNING);
        startTimer(roomId);
        // NAYA: WIN se wapas RUNNING me aane ka transition bhi persist
        // karo - taaki frontend ka "winner announcement" turant clear
        // ho jaaye aur agla number aana shuru ho jaaye UI me.
        supabaseRealtimeService.upsertGameState(state);
    }
}