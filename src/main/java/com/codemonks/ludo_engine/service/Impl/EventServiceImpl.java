package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.dto.common.EventDTO;
import com.codemonks.ludo_engine.enums.EventTypeEnum;
import com.codemonks.ludo_engine.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    @Override
   public EventDTO createEvent(EventTypeEnum type, String message){

        return new EventDTO(type,message);
    }
}
