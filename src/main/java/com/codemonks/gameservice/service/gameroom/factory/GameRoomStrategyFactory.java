package com.codemonks.gameservice.service.gameroom.factory;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.service.gameroom.GameRoomStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameRoomStrategyFactory {

    private final Map<GameTypeEnum, GameRoomStrategy> strategyMap;

    public GameRoomStrategyFactory(List<GameRoomStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        GameRoomStrategy::supports,
                        Function.identity()));
    }

    public GameRoomStrategy getStrategy(GameTypeEnum gameType) {
        GameRoomStrategy strategy = strategyMap.get(gameType);
        if (strategy == null) {
            throw new GameException(ResponseErrorCodes.GAME_ENGINE_NOT_FOUND);
        }
        return strategy;
    }
}