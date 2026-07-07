package com.codemonks.ludo_game_engine.dto.common;

import com.codemonks.ludo_game_engine.enums.PlayerColorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    private Long playerId;
    private PlayerColorEnum color;
    //Ludo uses color-specific rules: like R,G,B,Y and each have specific path and home
    private List<TokenDTO> tokens;
    //Each player owns: 4 tokens,Engine checks all.
    private List<Integer> pendingDice;
    private Boolean bot; //for future purpose
    private String botDifficulty;
}
