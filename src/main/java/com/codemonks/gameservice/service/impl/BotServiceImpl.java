package com.codemonks.gameservice.service.impl;


import com.codemonks.gameservice.entity.PlayerEntity;
import com.codemonks.gameservice.enums.RoomPlayerRole;
import com.codemonks.gameservice.service.BotService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotServiceImpl implements BotService {

    @Override
    public Long getNextBotUserId(List<PlayerEntity> players) {

        long nextBotUserId = -1L;

        for (PlayerEntity player : players) {

            if (player.getRole() == RoomPlayerRole.BOT
                    && player.getUserId() <= nextBotUserId) {

                nextBotUserId = player.getUserId() - 1;
            }
        }

        return nextBotUserId;
    }
}