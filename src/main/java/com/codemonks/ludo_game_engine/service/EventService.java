package com.codemonks.ludo_game_engine.service;

import com.codemonks.ludo_game_engine.dto.common.EventDTO;
import com.codemonks.ludo_game_engine.enums.EventTypeEnum;

public interface EventService {

    EventDTO createEvent(EventTypeEnum type, String message);

}
