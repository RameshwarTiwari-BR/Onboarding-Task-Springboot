package com.learning.springbootonboarding.day12.dto;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt
) {}
