/*

package com.featureflag.system.repository;

import com.featureflag.system.model.FeatureEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeatureEventRepository
        extends JpaRepository<FeatureEventEntity, Long> {

    // Existing Methods
    List<FeatureEventEntity> findByFlagKey(String flagKey);

    long countByFlagKeyAndVariant(String flagKey, String variant);

    // ✅ NEW: Exposure count grouped by variant
    @Query("""
        SELECT f.variant, COUNT(f)
        FROM FeatureEventEntity f
        WHERE f.flagKey = :flagKey
        GROUP BY f.variant
    """)
    List<Object[]> countUsersByVariant(@Param("flagKey") String flagKey);
}*/

package com.featureflag.system.repository;

import com.featureflag.system.model.FeatureEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeatureEventRepository
        extends JpaRepository<FeatureEventEntity, Long> {

    // ✅ Find all events for a flag
    List<FeatureEventEntity> findByFlagKey(String flagKey);

    // ✅ Count exposure rows (basic)
    long countByFlagKeyAndVariant(
            String flagKey,
            String variant
    );

    // ✅ UNIQUE USERS per variant
    @Query("""
        SELECT f.variant,
               COUNT(DISTINCT f.userId)
        FROM FeatureEventEntity f
        WHERE f.flagKey = :flagKey
        GROUP BY f.variant
    """)
    List<Object[]> countUsersByVariant(
            @Param("flagKey") String flagKey
    );

    // ✅ Total unique users for a flag
    @Query("""
        SELECT COUNT(DISTINCT f.userId)
        FROM FeatureEventEntity f
        WHERE f.flagKey = :flagKey
    """)
    long countUniqueUsersByFlag(
            @Param("flagKey") String flagKey
    );

    // ✅ Unique users for specific variant
    @Query("""
        SELECT COUNT(DISTINCT f.userId)
        FROM FeatureEventEntity f
        WHERE f.flagKey = :flagKey
        AND f.variant = :variant
    """)
    long countUniqueUsersByVariant(
            @Param("flagKey") String flagKey,
            @Param("variant") String variant
    );
}