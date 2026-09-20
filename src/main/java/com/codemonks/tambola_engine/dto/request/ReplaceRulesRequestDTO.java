package com.codemonks.tambola_engine.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceRulesRequestDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    private List<RuleConfigRequestDTO> rules;
}