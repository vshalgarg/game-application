package com.codemonks.tambola_engine.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PUT /rules ka body — host ne waiting-room me jo rules save kiye,
 * unka poora (replace-semantics) snapshot. Engine is room ke puraane
 * rules Supabase se delete karke inhi ko insert karta hai.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceRulesRequestDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    private List<RuleConfigRequestDTO> rules;
}