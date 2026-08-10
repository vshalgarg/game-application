package com.codemonks.ludo_engine.bot.factory;

import com.codemonks.ludo_engine.bot.strategy.BotStrategy;
import com.codemonks.ludo_engine.bot.strategy.EasyRandomBotStrategy;
import com.codemonks.ludo_engine.bot.strategy.MediumBotStrategy;
import com.codemonks.ludo_engine.bot.strategy.HardBotStrategy;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.enums.BotDifficultyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ludoBotStrategyFactory")
public class BotStrategyFactory {

    private final EasyRandomBotStrategy easyRandomBotStrategy;
    private final MediumBotStrategy mediumBotStrategy;
    private final HardBotStrategy hardBotStrategy;

    public BotStrategy getStrategy(GameStateDTO gameState, Long botPlayerId) {

        PlayerDTO botPlayer = gameState.getPlayers()
                .stream()
                .filter(player -> player.getPlayerId().equals(botPlayerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bot player not found: " + botPlayerId));

        BotDifficultyEnum difficulty = botPlayer.getBotDifficulty();

        if (difficulty == null) {
            return easyRandomBotStrategy;
        }

        return switch (difficulty) {
            case EASY -> easyRandomBotStrategy;
            case MEDIUM -> mediumBotStrategy;
            case HARD -> hardBotStrategy;
        };
    }
}