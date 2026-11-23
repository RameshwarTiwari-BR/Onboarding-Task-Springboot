package com.learning.springbootonboarding.day12.dto;

import com.learning.springbootonboarding.day12.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        UserStatus status,
        Instant createdAt,
        TokenResponse tokens
) {}