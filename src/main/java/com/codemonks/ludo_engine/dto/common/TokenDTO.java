package com.codemonks.ludo_engine.dto.common;

import com.codemonks.ludo_engine.enums.PlayerColorEnum;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
public class TokenDTO {

    private Long tokenId; //  we can make it String for frontend display debugging and logs animation

    private TokenStateEnum state; //Can be:BASE,TRACK,HOME_PATH,FINISHED

    private Integer position;
    //Rules depend heavily on state: // position Can be://BASE//TRACK//HOME_PATH//FINISHED

        private PlayerColorEnum color;
        // Token ka apna color — TRACK_START aur HOME_PATH entry distance calculate karne ke liye zaroori hai


}
