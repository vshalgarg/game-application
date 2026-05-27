package com.codemonks.gameservice.engineModule.dto.realtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LobbyPlayerDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("role")
    private String role;

    @JsonProperty("status")
    private String status;
}
