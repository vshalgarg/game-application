package com.codemonks.tambola_engine.repository;


import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import java.util.List;

public interface TambolaRuleRepository {

    GameRule findByRoomAndType(Long roomId, RuleTypeEnum ruleType);

    List<GameRule> findAllByRoom(Long roomId);

    void insertAll(List<GameRule> rules);

    boolean updateWinnersIfVersionMatches(
            Long roomId, RuleTypeEnum ruleType,
            List<Long> newWinnerPlayerIds, Long expectedVersion);
}