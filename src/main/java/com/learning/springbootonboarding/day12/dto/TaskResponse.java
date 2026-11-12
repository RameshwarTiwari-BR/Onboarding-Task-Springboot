package com.learning.springbootonboarding.day12.dto;

// What our API outputs securely to the client
public record TaskResponse(
        Long id,
        String name,
        boolean status,
        String timestamp
) {}
