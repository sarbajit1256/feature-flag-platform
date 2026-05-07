package com.featureflag.system.dto;

public class ConversionRequest {

    private String userId;
    private String variant;

    public String getUserId() {
        return userId;
    }

    public String getVariant() {
        return variant;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
}