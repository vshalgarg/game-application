package com.codemonks.gameservice.engineModule.factory;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.engineModule.strategy.GameEngine;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.exceptions.GameException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameEngineFactory {

    private final Map<GameTypeEnum, GameEngine> strategyMap;

    public GameEngineFactory(
            List<GameEngine> strategies
    ) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        GameEngine::supports,
                        Function.identity()
                ));
    }

    public GameEngine getStrategy(
            GameTypeEnum gameType
    ) {
        GameEngine strategy =
                strategyMap.get(gameType);
        if(strategy == null) {
            throw new GameException(
                    ResponseErrorCodes.GAME_ENGINE_NOT_FOUND
            );
        }
        return strategy;
    }
}
