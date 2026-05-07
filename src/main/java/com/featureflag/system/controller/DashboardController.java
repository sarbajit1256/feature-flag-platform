package com.featureflag.system.controller;

import com.featureflag.system.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final AnalyticsService analyticsService;

    public DashboardController(
            AnalyticsService analyticsService
    ) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{flagKey}")
    public String dashboard(
            @PathVariable String flagKey,
            Model model
    ) {

        Map<String, Object> analytics =
                analyticsService.getAnalytics(flagKey);

        model.addAttribute(
                "flagKey",
                flagKey
        );

        model.addAttribute(
                "analytics",
                analytics
        );

        return "dashboard";
    }
}