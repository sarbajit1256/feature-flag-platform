package com.featureflag.system.controller;

import com.featureflag.system.service.AutoRolloutService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rollout")
public class AutoRolloutController {

    private final AutoRolloutService autoRolloutService;

    public AutoRolloutController(
            AutoRolloutService autoRolloutService
    ) {
        this.autoRolloutService = autoRolloutService;
    }

    @PostMapping("/{flagKey}")
    public String triggerRollout(
            @PathVariable String flagKey
    ) {

        autoRolloutService.evaluateAndRollout(flagKey);

        return "Auto rollout executed for: " + flagKey;
    }
}