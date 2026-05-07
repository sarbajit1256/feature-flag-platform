/*
package com.featureflag.system.service;

import com.featureflag.system.repository.ConversionRepository;
import com.featureflag.system.repository.FeatureEventRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    private static final long MIN_USERS_FOR_WINNER = 5;

    private final ConversionRepository conversionRepo;
    private final FeatureEventRepository eventRepo;

    public AnalyticsService(
            ConversionRepository conversionRepo,
            FeatureEventRepository eventRepo
    ) {
        this.conversionRepo = conversionRepo;
        this.eventRepo = eventRepo;
    }

    public Map<String, Object> getAnalytics(String flagKey) {

        // =========================
        // 1️⃣ Exposure Data
        // =========================
        List<Object[]> exposureResults =
                eventRepo.countUsersByVariant(flagKey);

        // =========================
        // 2️⃣ Conversion Data
        // =========================
        List<Object[]> conversionResults =
                conversionRepo.countConversionsByVariant(flagKey);

        // =========================
        // 3️⃣ Store Maps
        // =========================
        Map<String, Long> usersMap =
                new HashMap<>();

        Map<String, Long> conversionsMap =
                new HashMap<>();

        for (Object[] row : exposureResults) {

            usersMap.put(
                    (String) row[0],
                    (Long) row[1]
            );
        }

        for (Object[] row : conversionResults) {

            conversionsMap.put(
                    (String) row[0],
                    (Long) row[1]
            );
        }

        // =========================
        // 4️⃣ Analytics Response
        // =========================
        Map<String, Object> response =
                new LinkedHashMap<>();

        String winner = null;

        double bestRate = -1;

        long totalUsers = 0;
        long totalConversions = 0;

        // =========================
        // 5️⃣ Per Variant Analytics
        // =========================
        for (String variant : usersMap.keySet()) {

            // ❌ Ignore OFF users
            if ("OFF".equalsIgnoreCase(variant)) {
                continue;
            }

            long users =
                    usersMap.getOrDefault(variant, 0L);

            long conversions =
                    conversionsMap.getOrDefault(variant, 0L);

            totalUsers += users;
            totalConversions += conversions;

            // ✅ Conversion Rate
            double rate = (users == 0)
                    ? 0
                    : ((double) conversions / users) * 100;

            // ✅ Rounded
            double roundedRate =
                    Math.round(rate * 100.0) / 100.0;

            // =========================
            // Variant Data
            // =========================
            Map<String, Object> data =
                    new LinkedHashMap<>();

            data.put("users", users);

            data.put("conversions", conversions);

            data.put("conversionRate", roundedRate);

            response.put(variant, data);

            // =========================
            // Winner Logic
            // =========================
            if (users >= MIN_USERS_FOR_WINNER
                    && rate > bestRate) {

                bestRate = rate;
                winner = variant;
            }
        }

        // =========================
        // 6️⃣ Experiment Summary
        // =========================
        Map<String, Object> summary =
                new LinkedHashMap<>();

        summary.put("totalUsers", totalUsers);

        summary.put(
                "totalConversions",
                totalConversions
        );

        summary.put(
                "winningConversionRate",
                bestRate < 0
                        ? 0
                        : Math.round(bestRate * 100.0) / 100.0
        );

        response.put("summary", summary);

        // =========================
        // 7️⃣ Winner
        // =========================
        response.put(
                "winner",
                winner == null
                        ? "NO_WINNER_YET"
                        : winner
        );

        return response;
    }
}*/


package com.featureflag.system.service;

import com.featureflag.system.repository.ConversionRepository;
import com.featureflag.system.repository.FeatureEventRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    private static final long MIN_USERS_FOR_WINNER = 5;

    private final ConversionRepository conversionRepo;
    private final FeatureEventRepository eventRepo;
    private final StatisticsService statisticsService;

    public AnalyticsService(
            ConversionRepository conversionRepo,
            FeatureEventRepository eventRepo,
            StatisticsService statisticsService
    ) {
        this.conversionRepo = conversionRepo;
        this.eventRepo = eventRepo;
        this.statisticsService = statisticsService;
    }

    public Map<String, Object> getAnalytics(String flagKey) {

        // =========================
        // 1️⃣ Exposure Data
        // =========================
        List<Object[]> exposureResults =
                eventRepo.countUsersByVariant(flagKey);

        // =========================
        // 2️⃣ Conversion Data
        // =========================
        List<Object[]> conversionResults =
                conversionRepo.countConversionsByVariant(flagKey);

        // =========================
        // 3️⃣ Store Maps
        // =========================
        Map<String, Long> usersMap =
                new HashMap<>();

        Map<String, Long> conversionsMap =
                new HashMap<>();

        for (Object[] row : exposureResults) {

            usersMap.put(
                    (String) row[0],
                    (Long) row[1]
            );
        }

        for (Object[] row : conversionResults) {

            conversionsMap.put(
                    (String) row[0],
                    (Long) row[1]
            );
        }

        // =========================
        // 4️⃣ Analytics Response
        // =========================
        Map<String, Object> response =
                new LinkedHashMap<>();

        String winner = null;

        double bestRate = -1;

        long totalUsers = 0;
        long totalConversions = 0;

        // 🔥 Store best variant metrics
        long winnerUsers = 0;
        long winnerConversions = 0;

        // =========================
        // 5️⃣ Per Variant Analytics
        // =========================
        for (String variant : usersMap.keySet()) {

            // ❌ Ignore OFF users
            if ("OFF".equalsIgnoreCase(variant)) {
                continue;
            }

            long users =
                    usersMap.getOrDefault(variant, 0L);

            long conversions =
                    conversionsMap.getOrDefault(variant, 0L);

            totalUsers += users;
            totalConversions += conversions;

            // ✅ Conversion Rate
            double rate = (users == 0)
                    ? 0
                    : ((double) conversions / users) * 100;

            // ✅ Rounded
            double roundedRate =
                    Math.round(rate * 100.0) / 100.0;

            // =========================
            // Confidence Calculation
            // =========================
            double confidence = 50.0;

            if (winner != null) {

                confidence =
                        statisticsService.calculateConfidence(
                                users,
                                conversions,
                                winnerUsers,
                                winnerConversions
                        );
            }

            // =========================
            // Variant Data
            // =========================
            Map<String, Object> data =
                    new LinkedHashMap<>();

            data.put("users", users);

            data.put("conversions", conversions);

            data.put("conversionRate", roundedRate);

            data.put(
                    "confidence",
                    Math.round(confidence * 100.0) / 100.0
            );

            response.put(variant, data);

            // =========================
            // Winner Logic
            // =========================
            if (users >= MIN_USERS_FOR_WINNER
                    && rate > bestRate) {

                bestRate = rate;

                winner = variant;

                winnerUsers = users;

                winnerConversions = conversions;
            }
        }

        // =========================
        // 6️⃣ Experiment Summary
        // =========================
        Map<String, Object> summary =
                new LinkedHashMap<>();

        summary.put("totalUsers", totalUsers);

        summary.put(
                "totalConversions",
                totalConversions
        );

        summary.put(
                "winningConversionRate",
                bestRate < 0
                        ? 0
                        : Math.round(bestRate * 100.0) / 100.0
        );

        response.put("summary", summary);

        // =========================
        // 7️⃣ Winner
        // =========================
        response.put(
                "winner",
                winner == null
                        ? "NO_WINNER_YET"
                        : winner
        );

        // =========================
        // 8️⃣ Statistical Significance
        // =========================
        response.put(
                "statisticallySignificant",
                bestRate > 0
        );

        return response;
    }
}