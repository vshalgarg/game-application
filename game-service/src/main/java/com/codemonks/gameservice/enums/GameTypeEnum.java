package com.codemonks.gameservice.enums;

public enum GameTypeEnum {
    TIC_TAC_TOE(1),
    LUDO(2),
    CHESS(3);

    private final int code;

    GameTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static GameTypeEnum fromCode(int code) {
        for (GameTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid GameType code: " + code);
    }
}