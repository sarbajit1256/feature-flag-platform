package com.featureflag.system.service;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.model.TargetingRule;
import com.featureflag.system.repository.FeatureFlagRepository;
import com.featureflag.system.repository.TargetingRuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureFlagAdminService {

    private final FeatureFlagRepository flagRepo;
    private final TargetingRuleRepository ruleRepo;

    public FeatureFlagAdminService(FeatureFlagRepository flagRepo,
                                   TargetingRuleRepository ruleRepo) {
        this.flagRepo = flagRepo;
        this.ruleRepo = ruleRepo;
    }

    // 🔥 Update flag → Evict cache
    @CacheEvict(value = "flags", key = "#flag.flagKey")
    public FeatureFlag updateFlag(FeatureFlag flag) {
        return flagRepo.save(flag);
    }

    // 🔥 Update rules → Evict cache
    @CacheEvict(value = "rules", key = "#flagId")
    public List<TargetingRule> updateRules(Long flagId, List<TargetingRule> rules) {

        ruleRepo.deleteAll(ruleRepo.findByFlagIdOrderByPriorityDesc(flagId));

        for (TargetingRule rule : rules) {
            rule.setFlagId(flagId);
        }

        return ruleRepo.saveAll(rules);
    }
}