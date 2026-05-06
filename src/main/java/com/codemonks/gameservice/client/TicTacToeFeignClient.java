package com.codemonks.gameservice.client;

import com.codemonks.gameservice.config.FeignConfig;
import com.codemonks.gameservice.engineModule.dto.request.EngineMoveRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.EngineResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tic-tac-toe-engine",
        url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface TicTacToeFeignClient {

    @PostMapping("/engine/start")
    EngineResponseDTO start(@RequestBody EngineStartRequestDTO request);

    @PostMapping("/engine/move")
    EngineResponseDTO move(@RequestBody EngineMoveRequestDTO request);

}
