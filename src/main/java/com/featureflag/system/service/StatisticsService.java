package com.featureflag.system.service;

import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    // ✅ Z-Test Confidence Calculation
    public double calculateConfidence(
            long usersA,
            long conversionsA,
            long usersB,
            long conversionsB
    ) {

        // 🚨 Safety
        if (usersA == 0 || usersB == 0) {
            return 0;
        }

        // Conversion rates
        double p1 = (double) conversionsA / usersA;
        double p2 = (double) conversionsB / usersB;

        // Pooled probability
        double pooled =
                (double) (conversionsA + conversionsB)
                        / (usersA + usersB);

        // Standard error
        double standardError =
                Math.sqrt(
                        pooled * (1 - pooled)
                                * ((1.0 / usersA) + (1.0 / usersB))
                );

        // 🚨 Avoid divide by zero
        if (standardError == 0) {
            return 0;
        }

        // Z-score
        double zScore =
                Math.abs(p1 - p2) / standardError;

        // Approximate confidence %
        return zScoreToConfidence(zScore);
    }

    // ✅ Approximate Z-score → Confidence
    private double zScoreToConfidence(double z) {

        if (z >= 2.58) {
            return 99.0;
        }

        if (z >= 1.96) {
            return 95.0;
        }

        if (z >= 1.64) {
            return 90.0;
        }

        if (z >= 1.28) {
            return 80.0;
        }

        return 50.0;
    }
}