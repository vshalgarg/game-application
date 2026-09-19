package com.codemonks.gameservice.client;

import com.codemonks.gameservice.config.FeignConfig;
import com.codemonks.gameservice.engineModule.dto.realtime.RealtimeLobbyDTO;
import com.codemonks.gameservice.engineModule.dto.request.EngineStartGameRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaClaimRequestDTO;
import com.codemonks.gameservice.engineModule.dto.request.TambolaReplaceRulesRequestDTO;
import com.codemonks.gameservice.engineModule.dto.response.TambolaClaimResponseDTO;
import com.codemonks.gameservice.engineModule.dto.response.TambolaGameSetupResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tambola-game-engine",
        url = "${services.tambola-engine.base-url}",
        configuration = FeignConfig.class
)
public interface TambolaFeignClient {

    @PostMapping("${services.tambola-engine.endpoints.start-game}")
    TambolaGameSetupResponseDTO start(@RequestBody EngineStartGameRequestDTO request);   // FIXED

    @PostMapping("${services.tambola-engine.endpoints.submit-claim}")
    TambolaClaimResponseDTO submitClaim(@RequestBody TambolaClaimRequestDTO request);

    @PostMapping("${services.tambola-engine.endpoints.lobby}")
    void publishLobby(@RequestBody RealtimeLobbyDTO request);

    @PutMapping("${services.tambola-engine.endpoints.rules}")
    void replaceRules(@RequestBody TambolaReplaceRulesRequestDTO request);
}