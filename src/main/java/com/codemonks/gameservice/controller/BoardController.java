package com.codemonks.gameservice.controller;


import com.codemonks.gameservice.constants.ApiUrlConstants;
import com.codemonks.gameservice.dto.ApiResponse;
import com.codemonks.gameservice.dto.ResponseMessages;
import com.codemonks.gameservice.engineModule.model.BoardLayout;
import com.codemonks.gameservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiUrlConstants.Room.BASE)
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final RoomService roomService;

    @GetMapping("/{roomCode}/board-layout")
    public ResponseEntity<ApiResponse<BoardLayout>> getBoardLayout(
            @PathVariable String roomCode) {

        log.info("Board layout request received. roomCode={}", roomCode);

        BoardLayout boardLayout = roomService.getBoardLayout(roomCode);

        return ResponseEntity.ok(
                ApiResponse.success(
                        boardLayout,
                        ResponseMessages.BOARD_LAYOUT_FETCHED
                )
        );
    }
}