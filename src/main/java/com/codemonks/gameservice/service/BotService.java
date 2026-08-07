package com.codemonks.gameservice.service;

import com.codemonks.gameservice.entity.PlayerEntity;

import java.util.List;

public interface BotService {

    Long getNextBotUserId(List<PlayerEntity> players);

}