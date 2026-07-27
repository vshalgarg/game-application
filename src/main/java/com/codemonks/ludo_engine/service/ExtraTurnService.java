package com.codemonks.ludo_engine.service;

public interface ExtraTurnService {

    boolean hasExtraTurn(
            Integer consumedDice,
            boolean tokenKilled,
            boolean tokenFinished
    );
}
