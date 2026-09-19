package com.codemonks.gameservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "tambola_rule_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TambolaRuleConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_type", nullable = false, length = 50, unique = true)
    private String ruleType;

    @Column(name = "threshold")
    private Integer threshold;
}