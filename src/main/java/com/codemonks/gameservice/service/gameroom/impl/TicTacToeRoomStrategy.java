package com.codemonks.gameservice.service.gameroom.impl;

import com.codemonks.gameservice.dto.request.AddBotRequestDTO;
import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.service.gameroom.GameRoomStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TicTacToeRoomStrategy implements GameRoomStrategy {

    @Override
    public GameTypeEnum supports() {
        return GameTypeEnum.TIC_TAC_TOE;
    }

    @Override
    public void validateCreateRequest(CreateRoomRequestDTO request) {
        log.debug("[TTT_CREATE_VALIDATED] No extra validation required currently.");
    }

    @Override
    public void validateAddBotRequest(AddBotRequestDTO request) {
        log.debug("[TTT_ADD_BOT_VALIDATED] BotDifficulty={}", request.getBotDifficulty());
    }
}