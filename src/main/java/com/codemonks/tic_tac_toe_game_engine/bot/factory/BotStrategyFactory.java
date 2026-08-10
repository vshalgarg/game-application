package com.codemonks.tic_tac_toe_game_engine.bot.factory;

import com.codemonks.tic_tac_toe_game_engine.bot.enums.BotDifficultyEnum;
import com.codemonks.tic_tac_toe_game_engine.bot.strategy.BotStrategy;
import com.codemonks.tic_tac_toe_game_engine.bot.strategy.EasyRandomBotStrategy;
import com.codemonks.tic_tac_toe_game_engine.bot.strategy.HardBotStrategy;
import com.codemonks.tic_tac_toe_game_engine.bot.strategy.MediumBotStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ticTacToeBotStrategyFactory")
public class BotStrategyFactory {

    private final Map<BotDifficultyEnum, BotStrategy> strategies;

    public BotStrategyFactory(EasyRandomBotStrategy easy,
                              MediumBotStrategy medium,
                              HardBotStrategy hard) {
        this.strategies = Map.of(
                BotDifficultyEnum.EASY,   easy,
                BotDifficultyEnum.MEDIUM, medium,
                BotDifficultyEnum.HARD,   hard
        );
    }

    public BotStrategy getStrategy(BotDifficultyEnum difficulty) {

//        if (difficulty == null) {
//            return strategies.get(BotDifficultyEnum.EASY); // default
//        }

        BotStrategy strategy = strategies.get(difficulty);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }
        return strategy;
    }
}