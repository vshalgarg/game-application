package com.codemonks.tic_tac_toe_game_engine.service;


import com.codemonks.tic_tac_toe_game_engine.bot.constants.BotConstants;
import com.codemonks.tic_tac_toe_game_engine.dto.request.EngineMoveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
@Slf4j
public class BotMoveService {

    private static final long BOT_DELAY_MS = 1000;

@Autowired
@Lazy
private  TicTacToeEngineImpl ticTacToeEngine;

    @Async("botMoveExecutor")
    public void processBotMove(Long roomId) {

        try {
            Thread.sleep(BOT_DELAY_MS);
            EngineMoveRequestDTO botRequest = new EngineMoveRequestDTO();

            botRequest.setRoomId(roomId);
            botRequest.setUserId(BotConstants.BOT_USER_ID);

            ticTacToeEngine.makeMove(botRequest);

            log.info(
                    "[BOT_MOVE_TRIGGERED] roomId={}",
                    roomId
            );

        } catch (Exception e) {

            log.error(
                    "[BOT_MOVE_FAILED] roomId={}",
                    roomId,
                    e
            );
        }
    }
}