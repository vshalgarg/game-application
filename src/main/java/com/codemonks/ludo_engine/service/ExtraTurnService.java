package com.codemonks.ludo_engine.service;

public interface ExtraTurnService {

    boolean hasExtraTurn(
            boolean tokenKilled,
            boolean tokenFinished
    );
}
