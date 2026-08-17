package com.codemonks.ludo_engine.service;


public interface BotRoomLockService {

    boolean tryAcquire(Long roomId);
    void release(Long roomId);
}