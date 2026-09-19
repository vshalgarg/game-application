package com.codemonks.tambola_engine.service.impl;

import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.dto.request.RuleConfigRequestDTO;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import com.codemonks.tambola_engine.repository.TambolaRuleRepository;
import com.codemonks.tambola_engine.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final TambolaRuleRepository ruleRepository;

    @Override
    public void replaceRules(Long roomId, List<RuleConfigRequestDTO> ruleConfigs) {
        log.info("[RULES_PUT_REQUEST] Room:{} RuleCount:{}", roomId,
                ruleConfigs != null ? ruleConfigs.size() : 0);

        // Host ke latest selection daalne se pehle is room ke puraane
        // (stale) rules hata do — replace-semantics, koi row piche na bache.
        ruleRepository.deleteByRoom(roomId);

        if (ruleConfigs == null || ruleConfigs.isEmpty()) {
            log.info("[RULES_PUT_REPLACED] Room:{} Deleted:{} Inserted:{}",
                    roomId, true, 0);
            return;
        }

        List<GameRule> rules = buildActiveRules(roomId, ruleConfigs);
        ruleRepository.insertAll(rules);

        log.info("[RULES_PUT_REPLACED] Room:{} Deleted:{} Inserted:{}",
                roomId, true, rules.size());
    }

    private List<GameRule> buildActiveRules(Long roomId, List<RuleConfigRequestDTO> ruleConfigs) {
        List<GameRule> rules = new ArrayList<>();

        for (RuleConfigRequestDTO config : ruleConfigs) {
            GameRule rule = new GameRule();
            rule.setRoomId(roomId);
            rule.setRuleType(config.getRuleType());
            rule.setOrder(config.getOrder());
            rule.setMaxWinners(
                    config.getMaxWinners() != null && config.getMaxWinners() >= 1
                            ? config.getMaxWinners()
                            : 1
            );
            rule.setWinnerPlayerIds(new ArrayList<>());
            rule.setThreshold(resolveThreshold(config.getRuleType(), config.getThreshold()));
            rule.setVersion(0L);
            rules.add(rule);
        }

        return rules;
    }

    /**
     * Threshold is a static/master rule property. Engine-side default ke
     * against resolve hota hai — client/auth ya game-service kabhi bhi
     * arbitrary threshold nahi bhejega. EARLY_FIVE keliye 5 default.
     */
    private Integer resolveThreshold(RuleTypeEnum ruleType, Integer requested) {
        if (ruleType == RuleTypeEnum.EARLY_FIVE && (requested == null || requested < 1)) {
            return 5;
        }
        return requested;
    }
}