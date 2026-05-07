
package com.featureflag.system.engine;

import com.featureflag.system.dto.FeatureEvent;
import com.featureflag.system.kafka.KafkaProducerService;
import com.featureflag.system.model.*;
import com.featureflag.system.repository.UserRepository;
import com.featureflag.system.service.FeatureFlagService;
import com.featureflag.system.service.VariantService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeatureEvaluationEngine {

    private final FeatureFlagService flagService;
    private final UserRepository userRepo;
    private final VariantService variantService;
    private final KafkaProducerService kafkaProducer;
    private final StringRedisTemplate redisTemplate;

    public FeatureEvaluationEngine(FeatureFlagService flagService,
                                   UserRepository userRepo,
                                   VariantService variantService,
                                   KafkaProducerService kafkaProducer,
                                   StringRedisTemplate redisTemplate) {

        this.flagService = flagService;
        this.userRepo = userRepo;
        this.variantService = variantService;
        this.kafkaProducer = kafkaProducer;
        this.redisTemplate = redisTemplate;
    }

    // 🔥 MAIN METHOD
    public String getVariant(String flagKey, String userId) {

        String redisKey = "flag:" + userId + ":" + flagKey;

        // ✅ Exposure deduplication key
        String exposureKey = "exposure:" + userId + ":" + flagKey;

        // ✅ 1. STICKY ASSIGNMENT (Redis)
        String cachedVariant = redisTemplate.opsForValue().get(redisKey);

        // ✅ If cached → return same variant
        if (cachedVariant != null) {

            // ✅ Exposure event only FIRST TIME EVER
            sendExposureIfNeeded(
                    exposureKey,
                    userId,
                    flagKey,
                    cachedVariant
            );

            return cachedVariant;
        }

        // 2️⃣ Fetch Feature Flag
        FeatureFlag flag = flagService.getFlag(flagKey);

        if (flag == null || flag.getStatus() == Status.OFF) {
            return "OFF";
        }

        // 3️⃣ Fetch User
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4️⃣ Evaluate Targeting Rules
        List<TargetingRule> rules = flagService.getRules(flag.getId());

        if (!evaluateRules(rules, user)) {
            return "OFF";
        }

        // 5️⃣ Bucketing
        int bucket = Math.abs(userId.hashCode()) % 100;

        int rollout = flag.getRolloutPercentage();

        // 🚨 Rollout Safety
        if (rollout <= 0) {
            return "OFF";
        }

        if (bucket >= rollout) {
            return "OFF";
        }

        // ✅ Normalize bucket
        int normalizedBucket = (rollout == 100)
                ? bucket
                : (bucket * 100) / rollout;

        // 6️⃣ Fetch Variants
        List<FlagVariant> variants =
                variantService.getVariants(flag.getId());

        if (variants == null || variants.isEmpty()) {
            return "OFF";
        }

        // ✅ Validate distribution = 100
        int total = variants.stream()
                .mapToInt(FlagVariant::getPercentage)
                .sum();

        if (total != 100) {
            throw new RuntimeException(
                    "Variant distribution must sum to 100"
            );
        }

        // 7️⃣ Variant Selection
        int cumulative = 0;

        String selectedVariant = null;

        for (FlagVariant variant : variants) {

            cumulative += variant.getPercentage();

            if (normalizedBucket < cumulative) {
                selectedVariant = variant.getVariantName();
                break;
            }
        }

        // 🚨 Final Safety
        if (selectedVariant == null) {
            throw new RuntimeException(
                    "Variant selection failed"
            );
        }

        // 8️⃣ Store Sticky Variant
        redisTemplate.opsForValue()
                .set(redisKey,
                        selectedVariant,
                        Duration.ofHours(24));

        // ✅ 9️⃣ UNIQUE EXPOSURE TRACKING
        sendExposureIfNeeded(
                exposureKey,
                userId,
                flagKey,
                selectedVariant
        );

        return selectedVariant;
    }

    // ✅ UNIQUE EXPOSURE EVENT
    private void sendExposureIfNeeded(
            String exposureKey,
            String userId,
            String flagKey,
            String variant
    ) {

        // ✅ Already exposed before
        if (redisTemplate.hasKey(exposureKey)) {
            return;
        }

        // ✅ Store exposure marker (30 days)
        redisTemplate.opsForValue()
                .set(exposureKey,
                        "EXPOSED",
                        Duration.ofDays(30));

        // ✅ Send Kafka exposure event
        sendKafkaEvent(userId, flagKey, variant);
    }

    // 🔥 Rule Evaluation
    private boolean evaluateRules(
            List<TargetingRule> rules,
            User user
    ) {

        if (rules == null || rules.isEmpty()) {
            return true;
        }

        for (TargetingRule rule : rules) {

            if (!evaluateRule(rule, user)) {
                return false;
            }
        }

        return true;
    }

    // 🔍 Single Rule
    private boolean evaluateRule(
            TargetingRule rule,
            User user
    ) {

        if (rule.getAttribute() == null) {
            return true;
        }

        switch (rule.getAttribute()) {

            case "country":

                return user.getCountry() != null
                        && user.getCountry()
                        .equalsIgnoreCase(rule.getValue());

            case "device":

                return user.getDevice() != null
                        && user.getDevice()
                        .equalsIgnoreCase(rule.getValue());

            default:
                return true;
        }
    }

    // 🔥 Kafka Sender
    private void sendKafkaEvent(
            String userId,
            String flagKey,
            String variant
    ) {

        FeatureEvent event = new FeatureEvent(
                userId,
                flagKey,
                variant,
                LocalDateTime.now()
        );

        kafkaProducer.sendEvent(event);
    }

    // ✅ Used by ConversionController
    public String getStoredVariant(
            String flagKey,
            String userId
    ) {

        String redisKey =
                "flag:" + userId + ":" + flagKey;

        return redisTemplate.opsForValue().get(redisKey);
    }
}