package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.constant.GameConstants;
import com.codemonks.ludo_engine.service.BotTurnService;
import com.codemonks.ludo_engine.service.EngineService;
import com.codemonks.ludo_engine.service.TurnDelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TurnDelayServiceImpl implements TurnDelayService {

    private final EngineService engineService;
    private final BotTurnService botTurnService;
    private final TaskScheduler taskScheduler;

    @Override
    public void scheduleTurnContinuation(
            Long roomId,
            String roomCode,
            Long playerId
    ) {

        taskScheduler.schedule(
                () -> {

                    engineService.continueTurnAfterDelay(
                            roomId,
                            roomCode,
                            playerId
                    );

                    botTurnService.triggerBotIfNeeded(
                            roomId,
                            roomCode
                    );
                },
                Instant.now().plusMillis(
                        GameConstants.TURN_DELAY_MS
                )
        );
    }
}