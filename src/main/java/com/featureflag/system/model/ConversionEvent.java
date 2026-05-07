package com.featureflag.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConversionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String flagKey;
    private String variant;
    private String eventType;

    private LocalDateTime timestamp;
}