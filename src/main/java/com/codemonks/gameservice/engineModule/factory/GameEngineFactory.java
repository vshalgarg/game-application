package com.codemonks.gameservice.engineModule.factory;

import com.codemonks.gameservice.engineModule.strategy.GameEngineStrategy;
import com.codemonks.gameservice.enums.GameTypeEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GameEngineFactory {

    private final Map<GameTypeEnum, GameEngineStrategy> strategyMap =
            new EnumMap<>(GameTypeEnum.class);

    private final GameEngineStrategy ticTacToeEngineStrategy;
    private final GameEngineStrategy chessEngineStrategy;

    @PostConstruct
    void init() {
        strategyMap.put(GameTypeEnum.TIC_TAC_TOE, ticTacToeEngineStrategy);
        strategyMap.put(GameTypeEnum.CHESS, chessEngineStrategy);
    }

    public GameEngineStrategy getStrategy(GameTypeEnum gameType) {
        GameEngineStrategy strategy = strategyMap.get(gameType);

        if (strategy == null) {
            throw new RuntimeException("No engine for: " + gameType);
        }

        return strategy;
    }
}
