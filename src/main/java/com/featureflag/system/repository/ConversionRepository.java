package com.featureflag.system.repository;

import com.featureflag.system.model.ConversionEvent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversionRepository extends JpaRepository<ConversionEvent, Long> {

    long countByFlagKeyAndVariant(String flagKey, String variant);
    boolean existsByUserIdAndFlagKey(String userId, String flagKey);
    
    @Query("SELECT c.variant, COUNT(c) FROM ConversionEvent c WHERE c.flagKey = :flagKey GROUP BY c.variant")
    List<Object[]> countConversionsByVariant(@Param("flagKey") String flagKey);
}