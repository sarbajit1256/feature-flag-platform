package com.featureflag.system.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flag_variants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FlagVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long flagId;
    private String variantName;
    private int percentage;
}