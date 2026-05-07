package com.featureflag.system.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FeatureEvent {

    private String userId;
    private String flagKey;
    private String variant;
    private LocalDateTime timestamp;
}
