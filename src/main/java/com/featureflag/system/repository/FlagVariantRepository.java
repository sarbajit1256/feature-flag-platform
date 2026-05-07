package com.featureflag.system.repository;

import com.featureflag.system.model.FlagVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlagVariantRepository extends JpaRepository<FlagVariant, Long> {

    List<FlagVariant> findByFlagId(Long flagId);
}