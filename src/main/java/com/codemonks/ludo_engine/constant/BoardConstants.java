package com.codemonks.ludo_engine.constant;


import com.codemonks.ludo_engine.enums.PlayerColorEnum;

import java.util.Map;
import java.util.Set;

public class BoardConstants {

    public static final Integer BOARD_SIZE=52; //Standard ludo track size

    public static final int HOME_PATH_SIZE = 6; // positions 0 to 5


    // because har color TRACK pe 50 cells travel karta  hai before entering its own home path.
    public static final int HOME_PATH_ENTRY_DISTANCE = 50;


    public static final Set<Integer> SAFE_CELLS=Set.of(0,8,13,21,26,34,39,47);
    //Token can not be killed on these safe places

    // Har color ka TRACK starting position (BASE→TRACK exit point)
    public static final Map<PlayerColorEnum, Integer> TRACK_START = Map.of(
            PlayerColorEnum.RED,    0,
            PlayerColorEnum.GREEN,  13,
            PlayerColorEnum.YELLOW, 26,
            PlayerColorEnum.BLUE,   39
    );

}
