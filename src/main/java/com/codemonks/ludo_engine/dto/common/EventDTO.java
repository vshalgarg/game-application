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
    //Engine may say:TOKEN_MOVED,KILL,EXTRA_TURN,Player won
    private String message;//Human-readable frontend message

}
