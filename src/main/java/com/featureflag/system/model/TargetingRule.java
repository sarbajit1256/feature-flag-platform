package com.featureflag.system.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "targeting_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
/*public class TargetingRule implements Serializable {
    private static final long serialVersionUID = 1L;*/
    public class TargetingRule{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long flagId;
    private String attribute;
    private String operator;
    private String value;
    private int priority;
}