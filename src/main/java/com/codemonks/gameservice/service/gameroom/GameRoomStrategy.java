package com.codemonks.gameservice.service.gameroom;


import com.codemonks.gameservice.dto.request.AddBotRequestDTO;
import com.codemonks.gameservice.dto.request.CreateRoomRequestDTO;
import com.codemonks.gameservice.enums.GameTypeEnum;

public interface GameRoomStrategy {

    GameTypeEnum supports();

    void validateCreateRequest(CreateRoomRequestDTO request);

    void validateAddBotRequest(AddBotRequestDTO request);
}