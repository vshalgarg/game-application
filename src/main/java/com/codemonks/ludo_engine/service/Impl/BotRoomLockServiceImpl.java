package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.service.BotRoomLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//Sufficient for a single-instance deployment. If this service is ever
//  horizontally scaled across multiple instances, this must be replaced
//  with a distributed lock implementation

@Service
@Slf4j
public class BotRoomLockServiceImpl implements BotRoomLockService {

    private final Set<Long> activeBotRooms = ConcurrentHashMap.newKeySet();

    @Override
    public boolean tryAcquire(Long roomId) {
        boolean acquired = activeBotRooms.add(roomId);

        if (acquired) {
            log.info("[BOT_LOCK_ACQUIRED] Room:{}", roomId);
        } else {
            log.warn("[BOT_LOCK_SKIPPED] Room:{} — a bot loop is already active, skipping duplicate trigger", roomId);
        }

        return acquired;
    }

    @Override
    public void release(Long roomId) {
        boolean wasLocked = activeBotRooms.remove(roomId);

        if (wasLocked) {
            log.info("[BOT_LOCK_RELEASED] Room:{}", roomId);
        }
    }
}