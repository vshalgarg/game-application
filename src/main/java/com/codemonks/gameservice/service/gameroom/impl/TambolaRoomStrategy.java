package com.codemonks.gameservice.service.gameroom.impl;

import com.codemonks.gameservice.dto.request.AddBotRequestDTO;
import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.service.gameroom.GameRoomStrategy;
import org.springframework.stereotype.Component;

@Component
public class TambolaRoomStrategy implements GameRoomStrategy {

    @Override
    public GameTypeEnum supports() {
        return GameTypeEnum.TAMBOLA;
    }

    @Override
    public void validateCreateRequest(CreateRoomRequestDTO request) {
        // Abhi ke liye koi Tambola-specific extra-validation zaroori
        // nahi hai (Ludo jaisa "botDifficulty must be set upfront"
        // jaisa constraint Tambola pe apply nahi hota). Room generic
        // fields (tenantId, userId, gameType) already controller-level
        // pe validate ho chuke maan rahe hain.
    }

    @Override
    public void validateAddBotRequest(AddBotRequestDTO request) {
        // Tambola abhi bots support nahi karta (multiplayer-only,
        // TambolaPlayerRequestDTO me isBot field hai future-proofing
        // ke liye, lekin abhi koi bot-player-generation logic nahi hai).
        throw new UnsupportedOperationException("Tambola does not support bot players yet");
    }
}