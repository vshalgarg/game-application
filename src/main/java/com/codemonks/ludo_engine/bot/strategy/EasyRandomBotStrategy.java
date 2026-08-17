package com.codemonks.ludo_engine.bot.strategy;

import com.codemonks.ludo_engine.dto.common.BotDecisionDTO;
import com.codemonks.ludo_engine.dto.common.GameStateDTO;
import com.codemonks.ludo_engine.dto.common.LegalMoveDTO;
import com.codemonks.ludo_engine.dto.common.PlayerDTO;
import com.codemonks.ludo_engine.service.AvailableMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@RequiredArgsConstructor
@Service("ludoEasyRandomBotStrategy")
public class EasyRandomBotStrategy implements BotStrategy {

    private final AvailableMoveService availableMoveService;

    @Override
    public BotDecisionDTO chooseMove(
            GameStateDTO gameState,
            Long botPlayerId,
            List<Integer> pendingDice
    ) {

        log.info("[BOT_MOVE_SELECTION_STARTED] Bot:{} Dice:{}", botPlayerId, pendingDice);
        PlayerDTO botPlayer = gameState.getPlayers()
                .stream()
                .filter(player -> player.getPlayerId().equals(botPlayerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bot player not found: " + botPlayerId));

        List<LegalMoveDTO> legalMoves = availableMoveService.getAvailableMoves(
                gameState, botPlayer, pendingDice);

        if (legalMoves.isEmpty()) {
            log.info("[BOT_NO_LEGAL_MOVE] Bot:{} Dice:{}", botPlayerId, pendingDice);
            return BotDecisionDTO.builder()
                    .moveAvailable(false)
                    .move(null)
                    .build();
        }
        LegalMoveDTO selectedMove = legalMoves.get(
                ThreadLocalRandom.current().nextInt(legalMoves.size()));
        log.info(
                "[BOT_MOVE_SELECTED] Bot:{} Token:{} Dice:{}",
                botPlayerId,
                selectedMove.getTokenId(),
                selectedMove.getDice()
        );
        return BotDecisionDTO.builder()
                .moveAvailable(true)
                .move(selectedMove)
                .build();
    }
}