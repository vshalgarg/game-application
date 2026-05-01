package com.codemonks.gameengine.dto.requestDto;

import com.codemonks.gameengine.dto.PlayerDTO;
import lombok.Data;

import java.util.List;

@Data
public class StartGameRequest {

    private List<PlayerDTO> players;
}
