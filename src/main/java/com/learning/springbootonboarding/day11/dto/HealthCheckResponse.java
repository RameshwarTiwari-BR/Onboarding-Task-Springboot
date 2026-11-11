package com.learning.springbootonboarding.day11.dto;

public record HealthCheckResponse (
        String status,
        String message,
        String timestamp
) {}
