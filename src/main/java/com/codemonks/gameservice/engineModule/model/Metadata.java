package com.codemonks.gameservice.engineModule.model;

import lombok.Data;

import java.util.List;

@Data
public class Metadata {

    private String boardName;

    private BoardSize boardSize;

    private Integer maxPlayers;

    private Integer tokensPerPlayer;
    private List<String> colors;

}