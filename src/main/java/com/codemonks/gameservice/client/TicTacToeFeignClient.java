package com.codemonks.gameservice.client;

import com.codemonks.gameservice.config.FeignConfig;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineGameStateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tic-tac-toe-engine",
        url = "${services.tic-tac-toe-engine.base-url}",
        configuration = FeignConfig.class
)
public interface TicTacToeFeignClient {

    @PostMapping("${services.tic-tac-toe-engine.endpoints.start-game}")
    EngineGameStateResponseDTO start(@RequestBody EngineStartGameRequestDTO request);

    @PostMapping("${services.tic-tac-toe-engine.endpoints.move}")
    EngineGameStateResponseDTO move(@RequestBody EngineMoveRequestDTO request);

}
