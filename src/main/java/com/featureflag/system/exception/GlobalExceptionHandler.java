package com.featureflag.system.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VariantMismatchException.class)
    public ResponseEntity<String> handleMismatch(VariantMismatchException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}