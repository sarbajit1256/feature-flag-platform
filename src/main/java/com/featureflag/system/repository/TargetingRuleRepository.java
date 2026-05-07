package com.featureflag.system.repository;

import com.featureflag.system.model.TargetingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetingRuleRepository extends JpaRepository<TargetingRule, Long> {
    List<TargetingRule> findByFlagIdOrderByPriorityDesc(Long flagId);
}