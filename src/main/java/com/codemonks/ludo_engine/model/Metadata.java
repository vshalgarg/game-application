package com.codemonks.ludo_engine.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@JsonPropertyOrder({"boardName", "boardSize", "colors", "maxPlayers", "tokensPerPlayer"})
@Data
public class Metadata {

    private String boardName;

    private BoardSize boardSize;

    private List<String> colors;

    private Integer maxPlayers;

    private Integer tokensPerPlayer;

}
