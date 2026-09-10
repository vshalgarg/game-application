package com.codemonks.tambola_engine.service;


public interface TimerService {
    void startTimer(Long roomId);
    void stopTimer(Long roomId);
    void scheduleResume(Long roomId, int delaySeconds);
}
