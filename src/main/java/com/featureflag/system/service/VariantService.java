package com.featureflag.system.service;

import com.featureflag.system.model.FlagVariant;
import com.featureflag.system.repository.FlagVariantRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariantService {

    private final FlagVariantRepository repo;

    public VariantService(FlagVariantRepository repo) {
        this.repo = repo;
    }

    @Cacheable(value = "variants", key = "#flagId")
    public List<FlagVariant> getVariants(Long flagId) {
        System.out.println("DB HIT → VARIANTS");
        return repo.findByFlagId(flagId);
    }
}