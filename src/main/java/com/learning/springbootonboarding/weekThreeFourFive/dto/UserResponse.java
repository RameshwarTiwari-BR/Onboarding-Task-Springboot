package com.learning.springbootonboarding.weekThreeFourFive.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.springbootonboarding.weekThreeFourFive.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        UserStatus status,
        Instant createdAt,
        TokenResponse tokens
) {}