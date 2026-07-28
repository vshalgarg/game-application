package com.codemonks.gameservice.client;


import com.codemonks.gameservice.config.FeignConfig;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.request.DiceRollRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.DiceRollResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.codemonks.gameservice.engineModule.model.BoardLayout;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "ludo-game-engine",
        url = "${services.ludo-engine.base-url}", // application.yml se dynamic base url lega
        configuration = FeignConfig.class)
public interface LudoFeignClient {

    @PostMapping("${services.ludo-engine.endpoints.start-game}")
    EngineGameStateResponseDTO start(@RequestBody EngineStartGameRequestDTO request);

    @PostMapping("${services.ludo-engine.endpoints.move}")
    EngineGameStateResponseDTO move(@RequestBody EngineMoveRequestDTO request);

    @PostMapping("${services.ludo-engine.endpoints.roll-dice}")
    DiceRollResponseDTO rollDice(@RequestBody DiceRollRequestDTO request);

    @PostMapping("${services.ludo-engine.endpoints.lobby}")
    void publishLobby(@RequestBody RealtimeLobbyDTO request);

    @GetMapping("${services.ludo-engine.endpoints.board-layout}")
    BoardLayout getBoardLayout();

}
