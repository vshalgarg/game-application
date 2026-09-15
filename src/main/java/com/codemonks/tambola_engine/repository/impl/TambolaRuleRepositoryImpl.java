package com.codemonks.tambola_engine.repository.impl;


import com.codemonks.tambola_engine.config.SupabaseProperties;
import com.codemonks.tambola_engine.domain.rule.GameRule;
import com.codemonks.tambola_engine.enums.RuleTypeEnum;
import com.codemonks.tambola_engine.exception.SupabaseStateException;
import com.codemonks.tambola_engine.repository.TambolaRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository("tambolaRuleRepositoryImpl")
@RequiredArgsConstructor
public class TambolaRuleRepositoryImpl implements TambolaRuleRepository {

    private final RestClient tambolaSupabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public GameRule findByRoomAndType(Long roomId, RuleTypeEnum ruleType) {
        String table = properties.getTables().getRealtimeTambolaRules();
        try {
            List<GameRule> result = tambolaSupabaseRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("room_id", "eq." + roomId)
                            .queryParam("rule_type", "eq." + ruleType)
                            .queryParam("select", "*")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GameRule>>() {});
            return (result == null || result.isEmpty()) ? null : result.get(0);
        } catch (Exception e) {
            log.error("Failed to fetch rule. roomId={} ruleType={}", roomId, ruleType, e);
            throw new SupabaseStateException("Failed to fetch rule for roomId=" + roomId, e);
        }
    }

    @Override
    public List<GameRule> findAllByRoom(Long roomId) {
        String table = properties.getTables().getRealtimeTambolaRules();
        try {
            List<GameRule> result = tambolaSupabaseRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("room_id", "eq." + roomId)
                            .queryParam("select", "*")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GameRule>>() {});
            return result == null ? List.of() : result;
        } catch (Exception e) {
            log.error("Failed to fetch rules. roomId={}", roomId, e);
            throw new SupabaseStateException("Failed to fetch rules for roomId=" + roomId, e);
        }
    }

    @Override
    public void insertAll(List<GameRule> rules) {
        if (rules.isEmpty()) return;
        String table = properties.getTables().getRealtimeTambolaRules();
        try {
            tambolaSupabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=minimal")
                    .body(rules)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to insert rules", e);
            throw new SupabaseStateException("Failed to insert rules", e);
        }
    }

    @Override
    public boolean updateWinnersIfVersionMatches(Long roomId, RuleTypeEnum ruleType,
                                                 List<Long> newWinnerPlayerIds, Long expectedVersion) {
        String table = properties.getTables().getRealtimeTambolaRules();
        try {
            List<Object> updatedRows = tambolaSupabaseRestClient.patch()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("room_id", "eq." + roomId)
                            .queryParam("rule_type", "eq." + ruleType)
                            .queryParam("version", "eq." + expectedVersion)
                            .build())
                    .header("Prefer", "return=representation")
                    .body(Map.of("winner_player_ids", newWinnerPlayerIds, "version", expectedVersion + 1))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Object>>() {});
            return updatedRows != null && !updatedRows.isEmpty();
        } catch (Exception e) {
            log.error("Failed to update rule-winners. roomId={} ruleType={}", roomId, ruleType, e);
            throw new SupabaseStateException("Failed to update rule for roomId=" + roomId, e);
        }
    }
}