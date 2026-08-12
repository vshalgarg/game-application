package com.codemonks.ludo_engine.service;


public interface BotTurnService {

    void triggerBotIfNeeded(
            Long roomId,
            String roomCode
    );
}