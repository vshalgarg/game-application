package com.codemonks.ludo_engine.bot.strategy;


import com.codemonks.ludo_engine.dto.common.BotDecisionDTO;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.LegalMoveDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.service.AvailableMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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

    private final AvailableMoveService availableMoveService;

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

        LegalMoveDTO chosen = legalMoves.stream()
                .max(Comparator.comparingDouble(this::score))
                .orElseThrow();

        log.info(
                "[HARD_BOT_MOVE_SELECTED] Bot:{} Token:{} Dice:{} score={} finish={} kill={} exitBase={} safe={}",
                botPlayerId, chosen.getTokenId(), chosen.getDice(), score(chosen),
                chosen.isReachesHome(), chosen.isKillsOpponent(),
                chosen.isExitsBase(), chosen.isLandsOnSafeCell()
        );

        return BotDecisionDTO.builder().moveAvailable(true).move(chosen).build();
    }

    private double score(LegalMoveDTO move) {

        double score = 0;

        if (move.isReachesHome()) score += FINISH_WEIGHT;
        if (move.isKillsOpponent()) score += KILL_WEIGHT;
        if (move.isLandsOnSafeCell()) score += SAFE_CELL_WEIGHT;
        if (move.isExitsBase()) score += EXIT_BASE_WEIGHT;

        if (move.getResultingPathIndex() != null) {
            score += move.getResultingPathIndex() * ADVANCEMENT_WEIGHT;
        }

        score += ThreadLocalRandom.current().nextDouble() * TIE_BREAK_RANDOMNESS;

        return score;
    }

    private PlayerDTO findPlayer(GameStateDTO gameState, Long playerId) {
        return gameState.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bot player not found: " + playerId));
    }
}