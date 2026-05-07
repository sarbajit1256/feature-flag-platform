package com.featureflag.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feature_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FeatureEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String flagKey;
    private String variant;

    private LocalDateTime timestamp;
    
}