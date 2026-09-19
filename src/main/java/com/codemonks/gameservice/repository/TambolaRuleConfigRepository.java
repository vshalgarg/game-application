package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.TambolaRuleConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TambolaRuleConfigRepository extends JpaRepository<TambolaRuleConfigEntity, Long> {

    Optional<TambolaRuleConfigEntity> findByRuleType(String ruleType);

    List<TambolaRuleConfigEntity> findAllByOrderByRuleTypeAsc();
}