/*package com.featureflag.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feature_flags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flag_key", unique = true)
    private String flagKey;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int rolloutPercentage;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}*/

package com.featureflag.system.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feature_flags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
/*public class FeatureFlag implements Serializable {

    private static final long serialVersionUID = 1L; */
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flagKey;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int rolloutPercentage;
}
