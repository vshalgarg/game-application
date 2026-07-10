package com.codemonks.ludo_game_engine.dto.realtime;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LobbyPlayerDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("role")
    private String role;

    @JsonProperty("status")
    private String status;
}