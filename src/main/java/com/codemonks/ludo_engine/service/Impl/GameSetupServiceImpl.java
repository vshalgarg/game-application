package com.codemonks.ludo_engine.service.Impl;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.common.TokenDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.ludo_engine.enums.GameStatusEnum;
import com.codemonks.ludo_engine.enums.PlayerColorEnum;
import com.codemonks.ludo_engine.enums.PlayerTurnStageEnum;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.service.GameSetupService;
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

        List<PlayerColorEnum> availableColors = selectColorsForPlayerCount(playerIds.size());

        //Create player one by one
        
        for (int playerIndex = 0; playerIndex < playerIds.size(); playerIndex++) {
            Long playerId = playerIds.get(playerIndex);
            PlayerColorEnum assignedColor = availableColors.get(playerIndex);
            PlayerDTO player = new PlayerDTO();

            player.setPlayerId(playerId);
            player.setColor(assignedColor);
            // Generate unique starting token id for this player
            long startingTokenId = (playerIndex * 4L) + 1;
            //Create tokens for player — color-aware, taaki TokenMovementService
            //inka TRACK_START aur HOME_PATH entry color se nikal sake
            player.setTokens(createTokens(assignedColor,startingTokenId));

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
    private List<TokenDTO> createTokens(PlayerColorEnum color, long startingTokenId) {

        List<TokenDTO> tokenList = new ArrayList<>();
        // Every player receives 4 unique tokens
        for (int i = 0; i < 4; i++) {

            TokenDTO token = new TokenDTO();

            token.setTokenId(startingTokenId + i);

            // Initial token state: inside base
            token.setState(TokenStateEnum.BASE);

            // Token
            token.setPosition(null);
            // Permanent base location
            token.setBaseSlot(i);
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

    private List<PlayerColorEnum> selectColorsForPlayerCount(int playerCount) {

        if (playerCount == 2) {
            List<List<PlayerColorEnum>> oppositePairs = List.of(
                    List.of(PlayerColorEnum.RED, PlayerColorEnum.YELLOW),
                    List.of(PlayerColorEnum.GREEN, PlayerColorEnum.BLUE)
            );

            List<PlayerColorEnum> chosenPair = new ArrayList<>(
                    oppositePairs.get(ThreadLocalRandom.current().nextInt(oppositePairs.size()))
            );

            Collections.shuffle(chosenPair);
            log.info("[2P_COLOR_SELECTION] Opposite pair chosen: {}", chosenPair);
            return chosenPair;
        }

        List<PlayerColorEnum> allColors = new ArrayList<>(Arrays.asList(PlayerColorEnum.values()));
        Collections.shuffle(allColors);
        return allColors;
    }
}