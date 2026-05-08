package com.codemonks.tic_tac_toe_game_engine.dto.request;

import com.codemonks.tic_tac_toe_game_engine.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EngineMoveRequestDTO {

    private Long gameId;
    private List<List<String>> boardState; // Generic List format
    private Long currentTurnUserId;
    private Long userId;
    private Map<String, Object> moveData; // Flexible move data
    private List<PlayerDto> players; // यह लिस्ट टर्न स्विच करने में मदद करेगी
}
