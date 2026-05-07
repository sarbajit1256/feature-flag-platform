/*package com.featureflag.system.service;

import com.featureflag.system.model.ConversionEvent;
import com.featureflag.system.repository.ConversionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConversionService {

    private final ConversionRepository repo;

    public ConversionService(ConversionRepository repo) {
        this.repo = repo;
    }

    public void trackConversion(String userId, String flagKey, String variant) {

        // ❌ Ignore OFF users
        if ("OFF".equals(variant)) {
            System.out.println("❌ Ignoring OFF conversion");
            return;
        }

        // ✅ Check if already converted (idempotency)
        boolean alreadyConverted = repo.existsByUserIdAndFlagKey(userId, flagKey);

        if (alreadyConverted) {
            System.out.println("⚠️ Duplicate conversion ignored for user: " + userId);
            return;
        }

        ConversionEvent entity = new ConversionEvent(
                null,
                userId,
                flagKey,
                variant,
                "CONVERSION",
                LocalDateTime.now()
        );

        try {
            repo.save(entity);
            System.out.println("✅ Conversion Tracked: " + variant);

        } catch (DataIntegrityViolationException ex) {
            // ✅ Handles race condition (parallel requests)
            System.out.println("⚠️ Duplicate prevented at DB level");
        }
    }
}*/

package com.featureflag.system.service;

import com.featureflag.system.model.ConversionEvent;
import com.featureflag.system.repository.ConversionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConversionService {

    private final ConversionRepository repo;

    public ConversionService(ConversionRepository repo) {
        this.repo = repo;
    }

    /**
     * @return ConversionResult (status of operation)
     */
    public ConversionResult trackConversion(String userId, String flagKey, String variant) {

        // ❌ Ignore OFF users
        if ("OFF".equals(variant)) {
            System.out.println("❌ Ignoring OFF conversion");
            return ConversionResult.IGNORED;
        }

        // ✅ Idempotency check (fast path)
        boolean alreadyConverted = repo.existsByUserIdAndFlagKey(userId, flagKey);

        if (alreadyConverted) {
            System.out.println("⚠️ Duplicate conversion ignored for user: " + userId);
            return ConversionResult.DUPLICATE;
        }

        ConversionEvent entity = new ConversionEvent(
                null,
                userId,
                flagKey,
                variant,
                "CONVERSION",
                LocalDateTime.now()
        );

        try {
            repo.save(entity);
            System.out.println("✅ Conversion Tracked: " + variant);
            return ConversionResult.SUCCESS;

        } catch (DataIntegrityViolationException ex) {
            // ✅ Handles race condition (parallel requests)
            System.out.println("⚠️ Duplicate prevented at DB level for user: " + userId);
            return ConversionResult.DUPLICATE;
        }
    }
}