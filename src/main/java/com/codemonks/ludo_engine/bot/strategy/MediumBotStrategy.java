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
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service("ludoMediumBotStrategy")
public class MediumBotStrategy implements BotStrategy {

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
            log.info("[MEDIUM_BOT_NO_LEGAL_MOVE] Bot:{} PendingDice:{}", botPlayerId, pendingDice);
            return BotDecisionDTO.builder().moveAvailable(false).build();
        }

        LegalMoveDTO chosen = pickByHierarchy(legalMoves);

        log.info(
                "[MEDIUM_BOT_MOVE_SELECTED] Bot:{} Token:{} Dice:{} finish={} kill={} exitBase={} safe={}",
                botPlayerId, chosen.getTokenId(), chosen.getDice(),
                chosen.isReachesHome(), chosen.isKillsOpponent(),
                chosen.isExitsBase(), chosen.isLandsOnSafeCell()
        );

        return BotDecisionDTO.builder().moveAvailable(true).move(chosen).build();
    }

    private LegalMoveDTO pickByHierarchy(List<LegalMoveDTO> moves) {

        Optional<LegalMoveDTO> finish = randomAmong(moves, LegalMoveDTO::isReachesHome);
        if (finish.isPresent()) return finish.get();

        Optional<LegalMoveDTO> kill = randomAmong(moves, LegalMoveDTO::isKillsOpponent);
        if (kill.isPresent()) return kill.get();

        Optional<LegalMoveDTO> exitBase = randomAmong(moves, LegalMoveDTO::isExitsBase);
        if (exitBase.isPresent()) return exitBase.get();

        Optional<LegalMoveDTO> safe = randomAmong(moves, LegalMoveDTO::isLandsOnSafeCell);
        if (safe.isPresent()) return safe.get();

        return moves.stream()
                .max(Comparator.comparing(m -> m.getResultingPathIndex() == null ? -1 : m.getResultingPathIndex()))
                .orElseGet(() -> moves.get(ThreadLocalRandom.current().nextInt(moves.size())));
    }

    private Optional<LegalMoveDTO> randomAmong(List<LegalMoveDTO> moves, Predicate<LegalMoveDTO> filter) {
        List<LegalMoveDTO> matches = moves.stream().filter(filter).collect(Collectors.toList());
        if (matches.isEmpty()) return Optional.empty();
        return Optional.of(matches.get(ThreadLocalRandom.current().nextInt(matches.size())));
    }

    private PlayerDTO findPlayer(GameStateDTO gameState, Long playerId) {
        return gameState.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bot player not found: " + playerId));
    }
}