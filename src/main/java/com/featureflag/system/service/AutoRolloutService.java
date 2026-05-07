package com.featureflag.system.service;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AutoRolloutService {

    private final AnalyticsService analyticsService;
    private final FeatureFlagRepository featureFlagRepository;

    public AutoRolloutService(
            AnalyticsService analyticsService,
            FeatureFlagRepository featureFlagRepository
    ) {
        this.analyticsService = analyticsService;
        this.featureFlagRepository = featureFlagRepository;
    }

    public void evaluateAndRollout(String flagKey) {

        Map<String, Object> analytics =
                analyticsService.getAnalytics(flagKey);

        String winner = (String) analytics.get("winner");

        // 🚨 No valid winner yet
        if (winner == null
                || "NO_WINNER_YET".equals(winner)) {

            System.out.println(
                    "❌ No winner yet for: " + flagKey
            );

            return;
        }

        Boolean significant =
                (Boolean) analytics.get(
                        "statisticallySignificant"
                );

        if (!Boolean.TRUE.equals(significant)) {

            System.out.println(
                    "❌ Experiment not statistically significant"
            );

            return;
        }

        FeatureFlag flag =
                featureFlagRepository
                        .findByFlagKey(flagKey)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Flag not found"
                                )
                        );

        int currentRollout = flag.getRolloutPercentage();

        int newRollout = determineNextRollout(currentRollout);

        // 🚨 Already fully rolled out
        if (newRollout == currentRollout) {

            System.out.println(
                    "✅ Already at maximum rollout"
            );

            return;
        }

        // ✅ Update rollout
        featureFlagRepository.updateRolloutPercentage(
                flagKey,
                newRollout
        );

        System.out.println(
                "🚀 Auto Rollout Updated: " +
                currentRollout + "% → " +
                newRollout + "%"
        );
    }

    // ✅ Progressive rollout strategy
    private int determineNextRollout(int current) {

        if (current < 10) {
            return 10;
        }

        if (current < 25) {
            return 25;
        }

        if (current < 50) {
            return 50;
        }

        if (current < 75) {
            return 75;
        }

        if (current < 100) {
            return 100;
        }

        return 100;
    }
}