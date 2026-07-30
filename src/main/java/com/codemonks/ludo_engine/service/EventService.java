package com.codemonks.ludo_engine.service;


import com.codemonks.ludo_engine.dto.common.EventDTO;
import com.codemonks.ludo_engine.enums.EventTypeEnum;

public interface EventService {

    EventDTO createEvent(EventTypeEnum type, String message);

}
