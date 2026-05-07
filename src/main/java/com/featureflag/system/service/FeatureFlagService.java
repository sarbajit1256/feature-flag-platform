/*package com.featureflag.system.service;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.model.TargetingRule;
import com.featureflag.system.repository.FeatureFlagRepository;
import com.featureflag.system.repository.TargetingRuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository flagRepo;
    private final TargetingRuleRepository ruleRepo;

    public FeatureFlagService(FeatureFlagRepository flagRepo,
                              TargetingRuleRepository ruleRepo) {
        this.flagRepo = flagRepo;
        this.ruleRepo = ruleRepo;
    }
    

    // ✅ Safe flag fetch (no exception caching)
    @Cacheable(value = "flags", key = "#flagKey", unless = "#result == null")
    public FeatureFlag getFlag(String flagKey) {

        System.out.println("📦 Fetching flag from DB...");

        Optional<FeatureFlag> optional = flagRepo.findByFlagKey(flagKey);

        return optional.orElse(null); // safer for caching
    }

    // ✅ Rules fetch with safe fallback
    @Cacheable(value = "rules", key = "#flagId", unless = "#result == null")
    public List<TargetingRule> getRules(Long flagId) {

        System.out.println("📦 Fetching rules from DB...");

        List<TargetingRule> rules = ruleRepo.findByFlagIdOrderByPriorityDesc(flagId);

        return rules != null ? rules : Collections.emptyList();
    }

    // 🔥 Cache eviction when flag updated
    @CacheEvict(value = "flags", key = "#flagKey")
    public void evictFlag(String flagKey) {
        System.out.println("🧹 Evicted flag cache: " + flagKey);
    }

    // 🔥 Cache eviction for rules
    @CacheEvict(value = "rules", key = "#flagId")
    public void evictRules(Long flagId) {
        System.out.println("🧹 Evicted rules cache: " + flagId);
    }
    
    
 // ✅ Fetch all experiments
    public List<FeatureFlag> getAllFlags() {

        return flagRepo.findAll();
    }

    // ✅ Save experiment
    public FeatureFlag saveFlag(FeatureFlag flag) {

        return flagRepo.save(flag);
    }
}*/

package com.featureflag.system.service;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.model.TargetingRule;
import com.featureflag.system.repository.FeatureFlagRepository;
import com.featureflag.system.repository.TargetingRuleRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository flagRepo;

    private final TargetingRuleRepository ruleRepo;

    public FeatureFlagService(
            FeatureFlagRepository flagRepo,
            TargetingRuleRepository ruleRepo
    ) {
        this.flagRepo = flagRepo;
        this.ruleRepo = ruleRepo;
    }

    // =========================
    // FETCH FLAG
    // =========================

    @Cacheable(
            value = "flags",
            key = "#flagKey",
            unless = "#result == null"
    )
    public FeatureFlag getFlag(String flagKey) {

        System.out.println(
                "📦 Fetching flag from DB..."
        );

        Optional<FeatureFlag> optional =
                flagRepo.findByFlagKey(flagKey);

        return optional.orElse(null);
    }

    // =========================
    // FETCH RULES
    // =========================

    @Cacheable(
            value = "rules",
            key = "#flagId",
            unless = "#result == null"
    )
    public List<TargetingRule> getRules(Long flagId) {

        System.out.println(
                "📦 Fetching rules from DB..."
        );

        List<TargetingRule> rules =
                ruleRepo.findByFlagIdOrderByPriorityDesc(flagId);

        return rules != null
                ? rules
                : Collections.emptyList();
    }

    // =========================
    // EVICT FLAG CACHE
    // =========================

    @CacheEvict(
            value = "flags",
            key = "#flagKey"
    )
    public void evictFlag(String flagKey) {

        System.out.println(
                "🧹 Evicted flag cache: " + flagKey
        );
    }

    // =========================
    // EVICT RULE CACHE
    // =========================

    @CacheEvict(
            value = "rules",
            key = "#flagId"
    )
    public void evictRules(Long flagId) {

        System.out.println(
                "🧹 Evicted rules cache: " + flagId
        );
    }

    // =========================
    // FETCH ALL FLAGS
    // =========================

    public List<FeatureFlag> getAllFlags() {

        return flagRepo.findAll();
    }

    // =========================
    // SAVE FLAG
    // =========================

    public FeatureFlag saveFlag(
            FeatureFlag flag
    ) {

        return flagRepo.save(flag);
    }

    // =========================
    // DELETE FLAG
    // =========================

    public void deleteFlag(Long id) {

        flagRepo.deleteById(id);
    }
}