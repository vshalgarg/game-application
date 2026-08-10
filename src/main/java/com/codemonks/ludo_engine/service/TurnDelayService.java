package com.codemonks.ludo_engine.service;


public interface TurnDelayService {

    void scheduleTurnContinuation(
            Long roomId,
            String roomCode,
            Long playerId
    );






}