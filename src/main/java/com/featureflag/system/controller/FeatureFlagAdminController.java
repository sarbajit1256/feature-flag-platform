package com.featureflag.system.controller;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.model.TargetingRule;
import com.featureflag.system.service.FeatureFlagAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class FeatureFlagAdminController {

    private final FeatureFlagAdminService adminService;

    public FeatureFlagAdminController(FeatureFlagAdminService adminService) {
        this.adminService = adminService;
    }

    // 🔥 Update flag
    @PutMapping("/flag")
    public FeatureFlag updateFlag(@RequestBody FeatureFlag flag) {
        return adminService.updateFlag(flag);
    }

    // 🔥 Update rules
    @PutMapping("/rules/{flagId}")
    public List<TargetingRule> updateRules(
            @PathVariable Long flagId,
            @RequestBody List<TargetingRule> rules) {

        return adminService.updateRules(flagId, rules);
    }
}