package com.codemonks.tambola_engine.service;

import com.codemonks.tambola_engine.dto.request.RuleConfigRequestDTO;

import java.util.List;

public interface RuleService {

    void replaceRules(Long roomId, List<RuleConfigRequestDTO> ruleConfigs);
}