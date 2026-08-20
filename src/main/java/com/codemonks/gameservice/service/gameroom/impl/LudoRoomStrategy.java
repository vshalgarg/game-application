package com.codemonks.gameservice.service.gameroom.impl;


import com.codemonks.gameservice.dto.request.AddBotRequestDTO;
import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.service.gameroom.GameRoomStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LudoRoomStrategy implements GameRoomStrategy {

    @Override
    public GameTypeEnum supports() {
        return GameTypeEnum.LUDO;
    }

    @Override
    public void validateCreateRequest(CreateRoomRequestDTO request) {
        // Abhi Ludo ke liye koi extra create-time validation nahi hai.
        // Future: agar request.getGameSpecificConfig() se kuch parse/validate karna ho, yahan aayega.
        log.debug("[LUDO_CREATE_VALIDATED] No extra validation required currently.");
    }

    @Override
    public void validateAddBotRequest(AddBotRequestDTO request) {
        // Ludo mein bot difficulty already top-level field hai (backward-compatible).
        log.debug("[LUDO_ADD_BOT_VALIDATED] BotDifficulty={}", request.getBotDifficulty());
    }
}