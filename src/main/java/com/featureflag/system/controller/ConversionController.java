
package com.featureflag.system.controller;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.model.Status;
import com.featureflag.system.service.ConversionResult;
import com.featureflag.system.service.ConversionService;
import com.featureflag.system.service.FeatureFlagService;
import com.featureflag.system.dto.ConversionRequest;
import com.featureflag.system.engine.FeatureEvaluationEngine;
import com.featureflag.system.exception.VariantMismatchException;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/conversion")
public class ConversionController {

    private final ConversionService service;
    private final FeatureEvaluationEngine engine;
    private final FeatureFlagService flagService;

    public ConversionController(ConversionService service,
                                FeatureEvaluationEngine engine,
                                FeatureFlagService flagService) {
        this.service = service;
        this.engine = engine;
        this.flagService = flagService;
    }

    @PostMapping("/{flagKey}")
    public String trackConversion(@PathVariable String flagKey,
                                 @RequestBody ConversionRequest request) {

        String userId = request.getUserId();
        String frontendVariant = request.getVariant();

        // 🔒 Basic validation
        if (userId == null || frontendVariant == null) {
            throw new RuntimeException("Missing userId or variant");
        }

        // 🔒 Validate flag exists & active
        FeatureFlag flag = flagService.getFlag(flagKey);
        if (flag == null || flag.getStatus() == Status.OFF) {
            return "Invalid or inactive experiment";
        }

        // 🔥 Get STORED variant (no re-evaluation)
        String actualVariant = engine.getStoredVariant(flagKey, userId);

        if (actualVariant == null) {
            return "User was not part of experiment";
        }

        // 🔒 Anti-tampering check
        if (!actualVariant.equals(frontendVariant)) {
            //throw new RuntimeException("Variant mismatch! Possible tampering.");
            throw new VariantMismatchException("Variant mismatch! Possible tampering.");
        }

        // ❌ Ignore OFF users
        if ("OFF".equals(actualVariant)) {
            return "User not in experiment";
        }

        // ✅ Save conversion
        ConversionResult result = service.trackConversion(userId, flagKey, actualVariant);

        switch (result) {
            case SUCCESS:
                return "Conversion recorded";
            case DUPLICATE:
                return "Duplicate conversion ignored";
            case IGNORED:
                return "User not eligible for conversion";
            default:
                return "Unknown state";
        }
    }
}