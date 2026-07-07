package com.codemonks.ludo_game_engine.service.Impl;


import com.codemonks.ludo_game_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_game_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_game_engine.dto.common.TokenDTO;

import com.codemonks.ludo_game_engine.dto.request.EngineStartGameRequestDTO;

import com.codemonks.ludo_game_engine.enums.GameStatusEnum;
import com.codemonks.ludo_game_engine.enums.PlayerColorEnum;
import com.codemonks.ludo_game_engine.enums.PlayerTurnStageEnum;
import com.codemonks.ludo_game_engine.enums.TokenStateEnum;

import com.codemonks.ludo_game_engine.service.GameSetupService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class GameSetupServiceImpl implements GameSetupService {

    @Override
    public GameStateDTO initializeGame(EngineStartGameRequestDTO request) {

        log.info("[GAME_SETUP_START] Room:{} Players:{}",
                request.getRoomCode(),
                request.getPlayerIds());

        //Create all players
        List<PlayerDTO> initializedPlayers = createPlayers(request.getPlayerIds());

        //Choose first turn randomly
        Long selectedFirstPlayerId = selectRandomPlayer(request.getPlayerIds());
        log.info(
                "[FIRST_TURN_SELECTED] Room:{} FirstPlayer:{}",
                request.getRoomCode(),
                selectedFirstPlayerId
        );

        //Create game state
        GameStateDTO gameState = new GameStateDTO();
        gameState.setPlayers(initializedPlayers);
        gameState.setCurrentTurnPlayerId(selectedFirstPlayerId);
        gameState.setWinnerPlayerId(null);
        gameState.setGameStatus(GameStatusEnum.RUNNING);
        gameState.setPlayerTurnStage(PlayerTurnStageEnum.ROLL_DICE);

        log.info("[GAME_SETUP_COMPLETED] Room:{} PlayerCount:{}",
                request.getRoomCode(),
                initializedPlayers.size());
        return gameState;
    }

    //Create player objects
    private List<PlayerDTO> createPlayers(List<Long> playerIds) {

        log.info("Player creation started");
        List<PlayerDTO> playerList = new ArrayList<>();
        //Get all available colors
        List<PlayerColorEnum> availableColors = new ArrayList<>(Arrays.asList(PlayerColorEnum.values()));
        //Shuffle colors randomly
        Collections.shuffle(availableColors);

        //Create player one by one
        for (int playerIndex = 0; playerIndex < playerIds.size(); playerIndex++) {
            Long playerId = playerIds.get(playerIndex);
            PlayerColorEnum assignedColor = availableColors.get(playerIndex);
            PlayerDTO player = new PlayerDTO();

            player.setPlayerId(playerId);
            player.setColor(assignedColor);
            //Create tokens for player — color-aware, taaki TokenMovementService
            //inka TRACK_START aur HOME_PATH entry color se nikal sake
            player.setTokens(createTokens(assignedColor));

            //initialize dice buffer
            player.setPendingDice(new ArrayList<>());
            playerList.add(player);
        }
        log.info("[PLAYER_CREATED] Player:{} Color:{}",
                playerIds,
                availableColors);

        return playerList;
    }
    //Create 4 tokens for each player
    private List<TokenDTO> createTokens(PlayerColorEnum color) {

        List<TokenDTO> tokenList = new ArrayList<>();
        //Every player receives:token 1,token 2,3,4
        for (long tokenNumber = 1; tokenNumber <= 4; tokenNumber++) {

            TokenDTO token = new TokenDTO();
            token.setTokenId(tokenNumber);
            //Initial token state:inside base
            token.setState(TokenStateEnum.BASE);

            //Base position
            token.setPosition(-1);
            token.setColor(color);
            tokenList.add(token);
        }
        return tokenList;
    }

    //Choose first player randomly
    private Long selectRandomPlayer(List<Long> playerIds) {
        int randomIndex = ThreadLocalRandom
                .current()
                .nextInt(playerIds.size());
        return playerIds.get(randomIndex);
    }
}