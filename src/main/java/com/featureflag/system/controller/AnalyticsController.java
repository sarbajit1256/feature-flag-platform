package com.featureflag.system.controller;

import com.featureflag.system.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/{flagKey}")
    public Map<String, Object> getAnalytics(@PathVariable String flagKey) {
        //return service.getConversionCounts(flagKey);
    	return service.getAnalytics(flagKey);
    }
}