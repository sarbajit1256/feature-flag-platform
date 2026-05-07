/*
package com.featureflag.system.controller;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.model.Status;
import com.featureflag.system.service.AutoRolloutService;
import com.featureflag.system.service.FeatureFlagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/experiments")
public class AdminExperimentController {

    private final FeatureFlagService featureFlagService;
    private final AutoRolloutService autoRolloutService;

    public AdminExperimentController(
            FeatureFlagService featureFlagService,
            AutoRolloutService autoRolloutService
    ) {
        this.featureFlagService = featureFlagService;
        this.autoRolloutService = autoRolloutService;
    }

    // =========================
    // LIST PAGE
    // =========================

    @GetMapping
    public String experiments(Model model) {

        model.addAttribute(
                "experiments",
                featureFlagService.getAllFlags()
        );

        return "admin-experiments";
    }

    // =========================
    // CREATE PAGE
    // =========================

    @GetMapping("/create")
    public String createPage(Model model) {

        model.addAttribute(
                "flag",
                new FeatureFlag()
        );

        return "create-experiment";
    }

    // =========================
    // SAVE EXPERIMENT
    // =========================

    @PostMapping("/create")
    public String createExperiment(
            @ModelAttribute FeatureFlag flag
    ) {

        featureFlagService.saveFlag(flag);

        return "redirect:/admin/experiments";
    }

    // =========================
    // PAUSE
    // =========================

    @PostMapping("/pause/{flagKey}")
    public String pauseExperiment(
            @PathVariable String flagKey
    ) {

        FeatureFlag flag =
                featureFlagService.getFlag(flagKey);

        flag.setStatus(Status.OFF);

        featureFlagService.saveFlag(flag);

        featureFlagService.evictFlag(flagKey);

        return "redirect:/admin/experiments";
    }

    // =========================
    // RESUME
    // =========================

    @PostMapping("/resume/{flagKey}")
    public String resumeExperiment(
            @PathVariable String flagKey
    ) {

        FeatureFlag flag =
                featureFlagService.getFlag(flagKey);

        flag.setStatus(Status.ON);

        featureFlagService.saveFlag(flag);

        featureFlagService.evictFlag(flagKey);

        return "redirect:/admin/experiments";
    }

    // =========================
    // AUTO ROLLOUT
    // =========================

    @PostMapping("/rollout/{flagKey}")
    public String rollout(
            @PathVariable String flagKey
    ) {

        autoRolloutService
                .evaluateAndRollout(flagKey);

        return "redirect:/admin/experiments";
    }
    
}*/


package com.featureflag.system.controller;

import com.featureflag.system.model.FeatureFlag;
import com.featureflag.system.model.Status;
import com.featureflag.system.service.AutoRolloutService;
import com.featureflag.system.service.FeatureFlagService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/experiments")
public class AdminExperimentController {

    private final FeatureFlagService featureFlagService;
    private final AutoRolloutService autoRolloutService;

    public AdminExperimentController(
            FeatureFlagService featureFlagService,
            AutoRolloutService autoRolloutService
    ) {
        this.featureFlagService = featureFlagService;
        this.autoRolloutService = autoRolloutService;
    }

    // =========================
    // LIST PAGE
    // =========================

    @GetMapping
    public String experiments(Model model) {

        model.addAttribute(
                "experiments",
                featureFlagService.getAllFlags()
        );

        return "admin-experiments";
    }

    // =========================
    // CREATE PAGE
    // =========================

    @GetMapping("/create")
    public String createPage(Model model) {

        model.addAttribute(
                "flag",
                new FeatureFlag()
        );

        return "create-experiment";
    }

    // =========================
    // SAVE EXPERIMENT
    // =========================

    @PostMapping("/create")
    public String createExperiment(
            @ModelAttribute FeatureFlag flag
    ) {

        featureFlagService.saveFlag(flag);

        featureFlagService.evictFlag(
                flag.getFlagKey()
        );

        return "redirect:/admin/experiments";
    }

    // =========================
    // PAUSE
    // =========================

    @PostMapping("/pause/{flagKey}")
    public String pauseExperiment(
            @PathVariable String flagKey
    ) {

        FeatureFlag flag =
                featureFlagService.getFlag(flagKey);

        if (flag == null) {

            return "redirect:/admin/experiments";
        }

        flag.setStatus(Status.OFF);

        featureFlagService.saveFlag(flag);

        featureFlagService.evictFlag(flagKey);

        return "redirect:/admin/experiments";
    }

    // =========================
    // RESUME
    // =========================

    @PostMapping("/resume/{flagKey}")
    public String resumeExperiment(
            @PathVariable String flagKey
    ) {

        FeatureFlag flag =
                featureFlagService.getFlag(flagKey);

        if (flag == null) {

            return "redirect:/admin/experiments";
        }

        flag.setStatus(Status.ON);

        featureFlagService.saveFlag(flag);

        featureFlagService.evictFlag(flagKey);

        return "redirect:/admin/experiments";
    }

    // =========================
    // AUTO ROLLOUT
    // =========================

    @PostMapping("/rollout/{flagKey}")
    public String rollout(
            @PathVariable String flagKey
    ) {

        autoRolloutService
                .evaluateAndRollout(flagKey);

        return "redirect:/admin/experiments";
    }

    // =========================
    // DELETE EXPERIMENT
    // =========================

    @PostMapping("/delete/{flagKey}")
    public String deleteExperiment(
            @PathVariable String flagKey
    ) {

        FeatureFlag flag =
                featureFlagService.getFlag(flagKey);

        if (flag != null) {

            featureFlagService.deleteFlag(
                    flag.getId()
            );

            featureFlagService.evictFlag(flagKey);
        }

        return "redirect:/admin/experiments";
    }
}