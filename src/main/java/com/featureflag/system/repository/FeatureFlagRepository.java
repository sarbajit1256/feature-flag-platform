/*package com.featureflag.system.repository;

import com.featureflag.system.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByFlagKey(String flagKey);
}*/

package com.featureflag.system.repository;

import com.featureflag.system.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FeatureFlagRepository
        extends JpaRepository<FeatureFlag, Long> {

    Optional<FeatureFlag> findByFlagKey(String flagKey);

    @Modifying
    @Transactional
    @Query("""
        UPDATE FeatureFlag f
        SET f.rolloutPercentage = :rollout
        WHERE f.flagKey = :flagKey
    """)
    void updateRolloutPercentage(
            @Param("flagKey") String flagKey,
            @Param("rollout") int rollout
    );
}