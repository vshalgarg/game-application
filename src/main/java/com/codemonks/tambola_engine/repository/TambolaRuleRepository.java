package com.codemonks.tambola_engine.repository;


import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import java.util.List;

public interface TambolaRuleRepository {

    GameRule findByRoomAndType(Long roomId, RuleTypeEnum ruleType);

    List<GameRule> findAllByRoom(Long roomId);

    void insertAll(List<GameRule> rules);

    /**
     * Agreed replace-semantics (PUT):
     * host ke latest selection ko daalne se PEHLE is room ke puraane
     * (stale) rules hata dete hain, taaki Supabase me stale rows na
     * pade reh jaayein.
     */
    void deleteByRoom(Long roomId);

    boolean updateWinnersIfVersionMatches(
            Long roomId, RuleTypeEnum ruleType,
            List<Long> newWinnerPlayerIds, Long expectedVersion);
}