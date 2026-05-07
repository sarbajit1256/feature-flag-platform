package com.featureflag.system.scheduler;

import com.featureflag.system.service.AutoRolloutService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExperimentScheduler {

    private final AutoRolloutService autoRolloutService;

    public ExperimentScheduler(
            AutoRolloutService autoRolloutService
    ) {
        this.autoRolloutService = autoRolloutService;
    }

    // ✅ Every 2 minutes
    @Scheduled(fixedRate = 120000)
    public void monitorExperiments() {

        System.out.println(
                "⏰ Running Auto Rollout Monitor"
        );

        autoRolloutService.evaluateAndRollout("new_ui");
    }
}