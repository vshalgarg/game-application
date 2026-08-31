package com.codemonks.ludo_engine.bot.strategy;

import com.codemonks.ludo_engine.dto.common.BotDecisionDTO;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.LegalMoveDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.dto.common.TokenDTO;
import com.codemonks.ludo_engine.enums.TokenStateEnum;
import com.codemonks.ludo_engine.service.AvailableMoveService;
import com.codemonks.ludo_engine.service.BoardService;
import com.codemonks.ludo_engine.service.PathOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@RequiredArgsConstructor
@Service("ludoHardBotStrategy")
public class HardBotStrategy implements BotStrategy {

    private static final double FINISH_WEIGHT = 1000;
    private static final double KILL_WEIGHT = 500;
    private static final double SAFE_CELL_WEIGHT = 150;
    private static final double EXIT_BASE_WEIGHT = 100;
    private static final double ADVANCEMENT_WEIGHT = 1;
    private static final double TIE_BREAK_RANDOMNESS = 5;
    private static final double DIVERSIFICATION_WEIGHT = 2;
    private static final double AMBUSH_PRESERVATION_PENALTY = 8;
    private static final int AMBUSH_DISTANCE_THRESHOLD = 10;
    private final AvailableMoveService availableMoveService;
    private final BoardService boardService;
    private final PathOrderService pathOrderService;

    @Override
    public BotDecisionDTO chooseMove(
            GameStateDTO gameState,
            Long botPlayerId,
            List<Integer> pendingDice
    ) {

        PlayerDTO botPlayer = findPlayer(gameState, botPlayerId);

        List<LegalMoveDTO> legalMoves =
                availableMoveService.getAvailableMoves(gameState, botPlayer, pendingDice);

        if (legalMoves.isEmpty()) {
            log.info("[HARD_BOT_NO_LEGAL_MOVE] Bot:{} PendingDice:{}", botPlayerId, pendingDice);
            return BotDecisionDTO.builder().moveAvailable(false).build();
        }

        double averageProgress = calculateAverageTrackProgress(botPlayer);

        LegalMoveDTO chosen = legalMoves.stream()
                .max(Comparator.comparingDouble(move ->
                        score(move, gameState, botPlayer, averageProgress)))
                .orElseThrow();

        log.info(
                "[HARD_BOT_MOVE_SELECTED] Bot:{} Token:{} Dice:{} score={} finish={} kill={} exitBase={} safe={}",
                botPlayerId, chosen.getTokenId(), chosen.getDice(),
                score(chosen, gameState, botPlayer, averageProgress),
                chosen.isReachesHome(), chosen.isKillsOpponent(),
                chosen.isExitsBase(), chosen.isLandsOnSafeCell()
        );

        return BotDecisionDTO.builder().moveAvailable(true).move(chosen).build();
    }

    private double score(
            LegalMoveDTO move,
            GameStateDTO gameState,
            PlayerDTO botPlayer,
            double averageProgress
    ) {

        double score = 0;

        if (move.isReachesHome()) score += FINISH_WEIGHT;
        if (move.isKillsOpponent()) score += KILL_WEIGHT;
        if (move.isLandsOnSafeCell()) score += SAFE_CELL_WEIGHT;
        if (move.isExitsBase()) score += EXIT_BASE_WEIGHT;

        if (move.getResultingPathIndex() != null) {
            score += move.getResultingPathIndex() * ADVANCEMENT_WEIGHT;
        }

        TokenDTO movingToken = findToken(botPlayer, move.getTokenId());

        if (movingToken != null && movingToken.getState() == TokenStateEnum.TRACK) {

            score += diversificationBonus(movingToken, averageProgress);

            if (!move.isReachesHome() && !move.isKillsOpponent()
                    && isAmbushWorthPreserving(gameState, botPlayer, movingToken)) {
                score -= AMBUSH_PRESERVATION_PENALTY;
            }
        }

        score += ThreadLocalRandom.current().nextDouble() * TIE_BREAK_RANDOMNESS;

        return score;
    }

    private double diversificationBonus(TokenDTO movingToken, double averageProgress) {

        Integer currentIndex = movingToken.getPathIndex();
        if (currentIndex == null) {
            return 0;
        }

        double behindBy = averageProgress - currentIndex;
        if (behindBy <= 0) {
            return 0;
        }

        return behindBy * DIVERSIFICATION_WEIGHT;
    }

    private boolean isAmbushWorthPreserving(
            GameStateDTO gameState,
            PlayerDTO botPlayer,
            TokenDTO myToken
    ) {

        if (myToken.getPathId() == null || myToken.getPathIndex() == null) {
            return false;
        }

        Set<Integer> safeCells = boardService.getSafeCells();
        boolean onSafeOrStart = safeCells.contains(myToken.getPathId());

        if (!onSafeOrStart) {
            return false;
        }

        Integer myPathOrder = pathOrderService.getPathOrder(gameState, botPlayer.getPlayerId());
        List<Integer> myPath = boardService.getPath(myPathOrder);

        if (myPath == null || myPath.isEmpty()) {
            return false;
        }

        for (PlayerDTO opponent : gameState.getPlayers()) {

            if (opponent.getPlayerId().equals(botPlayer.getPlayerId())) {
                continue;
            }

            for (TokenDTO opponentToken : opponent.getTokens()) {

                if (opponentToken.getState() != TokenStateEnum.TRACK
                        || opponentToken.getPathIndex() == null) {
                    continue;
                }


                Integer opponentPathOrder =
                        pathOrderService.getPathOrder(gameState, opponent.getPlayerId());
                List<Integer> opponentPath = boardService.getPath(opponentPathOrder);

                if (opponentPath == null || opponentPath.isEmpty()) {
                    continue;
                }

                if (opponentToken.getPathIndex() >= opponentPath.size()) {
                    continue;
                }

                Integer opponentCellId = opponentToken.getPathId();
                int myCellPositionOnOwnPath = myToken.getPathIndex();
                int opponentCellPositionOnOwnPath = myPath.indexOf(opponentCellId);

                if (opponentCellPositionOnOwnPath == -1) {
                    continue;
                }

                int distanceBehind = myCellPositionOnOwnPath - opponentCellPositionOnOwnPath;

                if (distanceBehind >= 1 && distanceBehind <= AMBUSH_DISTANCE_THRESHOLD) {
                    return true;
                }
            }
        }

        return false;
    }

    private double calculateAverageTrackProgress(PlayerDTO botPlayer) {

        List<TokenDTO> trackTokens = botPlayer.getTokens().stream()
                .filter(token -> token.getState() == TokenStateEnum.TRACK)
                .filter(token -> token.getPathIndex() != null)
                .toList();

        if (trackTokens.isEmpty()) {
            return 0;
        }

        return trackTokens.stream()
                .mapToInt(TokenDTO::getPathIndex)
                .average()
                .orElse(0);
    }

    private TokenDTO findToken(PlayerDTO player, Long tokenId) {
        return player.getTokens().stream()
                .filter(token -> token.getTokenId().equals(tokenId))
                .findFirst()
                .orElse(null);
    }

    private PlayerDTO findPlayer(GameStateDTO gameState, Long playerId) {
        return gameState.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bot player not found: " + playerId));
    }
}