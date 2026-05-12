package com.example.waterbill.exception;

public record ErrorResponse(
        int status,
        String error,
        String message,
        long timestamp
) {}
