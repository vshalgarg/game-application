package com.codemonks.ludo_engine.dto.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BotDecisionDTO {

    private final boolean moveAvailable;

    private final LegalMoveDTO move;
}