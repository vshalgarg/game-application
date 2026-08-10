package com.codemonks.ludo_engine.dto.common;

import com.codemonks.ludo_engine.enums.EventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private EventTypeEnum type;
    private String message;

}
