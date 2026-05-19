package com.codemonks.tic_tac_toe_game_engine.bot.factory;

import com.codemonks.tic_tac_toe_game_engine.bot.strategy.BotStrategy;
import com.codemonks.tic_tac_toe_game_engine.bot.strategy.EasyRandomBotStrategy;
import com.codemonks.tic_tac_toe_game_engine.enums.DifficultyLevel;
import org.springframework.stereotype.Component;

@Component
public class BotFactory {

    public BotStrategy getBotStrategy(DifficultyLevel difficultyLevel) {

        return switch (difficultyLevel) {
            case EASY -> new EasyRandomBotStrategy();

            default -> throw new IllegalArgumentException(
                    "Unsupported difficulty level"
            );
        };
    }
}