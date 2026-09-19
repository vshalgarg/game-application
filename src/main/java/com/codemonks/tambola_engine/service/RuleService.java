package com.codemonks.tambola_engine.service;

import com.codemonks.tambola_engine.dto.request.RuleConfigRequestDTO;

import java.util.List;

public interface RuleService {

    /**
     * Replace-semantics PUT: is room ke puraane rules Supabase se hata
     * kar host ke latest selection ko insert kar deta hai.
     */
    void replaceRules(Long roomId, List<RuleConfigRequestDTO> ruleConfigs);
}