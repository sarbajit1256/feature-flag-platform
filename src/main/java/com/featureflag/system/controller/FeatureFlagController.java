/*
package com.featureflag.system.controller;

import com.featureflag.system.engine.FeatureEvaluationEngine;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/flags")
public class FeatureFlagController {

    private final FeatureEvaluationEngine engine;

    public FeatureFlagController(FeatureEvaluationEngine engine) {
        this.engine = engine;
    }

    @PostMapping("/evaluate/{flagKey}")
    public Map<String, Object> evaluate(
            @PathVariable String flagKey,
            @RequestBody UserRequest request) {

        boolean enabled = engine.isFeatureEnabled(flagKey, request.getUserId());

        return Map.of("enabled", enabled);
    }

    @Data
    static class UserRequest {
        private String userId;
    }
}*/

package com.featureflag.system.controller;

import com.featureflag.system.engine.FeatureEvaluationEngine;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/flags")
public class FeatureFlagController {

    private final FeatureEvaluationEngine engine;

    public FeatureFlagController(FeatureEvaluationEngine engine) {
        this.engine = engine;
    }

   /* @PostMapping("/variant/{flagKey}")
    public Map<String, Object> getVariant(
            @PathVariable String flagKey,
            @RequestBody UserRequest request) {

        String variant = engine.getVariant(flagKey, request.getUserId());

        return Map.of("variant", variant);
    }*/
    
    @GetMapping("/variant/{flagKey}")
    public Map<String, Object> getVariant(
            @PathVariable String flagKey,
            @RequestParam String userId) {

        String variant = engine.getVariant(flagKey, userId);

        return Map.of("variant", variant);
    }

    @Data
    static class UserRequest {
        private String userId;
    }
}