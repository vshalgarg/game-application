package com.codemonks.ludo_engine.service.Impl;


import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.common.TokenDTO;
import com.codemonks.ludo_engine.dto.request.EngineStartGameRequestDTO;
import com.codemonks.ludo_engine.enums.GameStatusEnum;
import com.codemonks.ludo_engine.enums.PlayerTurnStageEnum;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.service.BoardService;
import com.codemonks.ludo_engine.service.GameSetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSetupServiceImpl implements GameSetupService {

    private final BoardService boardService;

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

    // 1=bottom-right, 2=bottom-left, 3=top-left, 4=top-right — true clockwise order
    private static final List<Integer> CORNER_ORDER = List.of(1, 2, 3, 4);

    //Create player objects
    private List<PlayerDTO> createPlayers(List<Long> playerIds) {

        log.info("Player creation started");
        List<PlayerDTO> playerList = new ArrayList<>();
        List<Integer> assignedColorIndexes = selectColorIndexes(playerIds.size());

        for (int playerIndex = 0; playerIndex < playerIds.size(); playerIndex++) {
            Long playerId = playerIds.get(playerIndex);

            Integer colorIndex = assignedColorIndexes.get(playerIndex);
            PlayerDTO player = new PlayerDTO();

            player.setPlayerId(playerId);
            player.setColorIndex(colorIndex);
            long startingTokenId = (playerIndex * 4L) + 1;
            player.setTokens(createTokens(startingTokenId, colorIndex));

            //initialize dice buffer
            player.setPendingDice(new ArrayList<>());
            player.setPendingExtraTurn(false);
            playerList.add(player);
        }
        playerList.sort(Comparator.comparingInt(p -> CORNER_ORDER.indexOf(p.getColorIndex())));

        log.info("[PLAYER_CREATED] Players:{} AssignedColorIndexes:{}  CornerOrdered:{}",
                playerIds,
                assignedColorIndexes,
                playerList.stream().map(PlayerDTO::getColorIndex).toList());

        return playerList;
    }

    //Create 4 tokens for each player
    private List<TokenDTO> createTokens(long startingTokenId,Integer colorIndex) {

        List<TokenDTO> tokenList = new ArrayList<>();

        int tokensPerPlayer = boardService
                .getBoard()
                .getMetadata()
                .getTokensPerPlayer();

        for (int i = 0; i < tokensPerPlayer; i++) {

            TokenDTO token = new TokenDTO();
            token.setTokenId(startingTokenId + i);
            token.setState(TokenStateEnum.BASE);
            token.setPathIndex(null);
            token.setBaseSlot(i);
            token.setBaseSlotId(boardService.getBaseSlotId(colorIndex, i));
            token.setForwardJourney(new ArrayList<>());
            token.setTokenKilled(false);
            tokenList.add(token);
            token.setBackwardJourney(new ArrayList<>());
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
    private List<Integer> selectColorIndexes(int playerCount) {

        int playableColors = boardService.getColors().size() - 1;

        if (playerCount > playableColors) {
            throw new IllegalArgumentException("Not enough colors defined in board-layout.json");
        }

        List<Integer> colorIndexes = new ArrayList<>();

        if (playerCount == 2) {

            List<List<Integer>> oppositePairs = List.of(
                    List.of(1, 3), // Yellow - red
                    List.of(2, 4)  // Blue - green
            );

            int randomPair = ThreadLocalRandom.current().nextInt(oppositePairs.size());

            return new ArrayList<>(oppositePairs.get(randomPair));
        }

        for (int i = 1; i <= playableColors; i++) {
            colorIndexes.add(i);
        }

        Collections.shuffle(colorIndexes);

        return colorIndexes.subList(0, playerCount);
    }
}