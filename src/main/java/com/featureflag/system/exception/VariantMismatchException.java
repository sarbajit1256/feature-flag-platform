package com.featureflag.system.exception;

public class VariantMismatchException extends RuntimeException {

    public VariantMismatchException(String message) {
        super(message);
    }
}