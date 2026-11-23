package com.learning.springbootonboarding.day12.dto;

import com.learning.springbootonboarding.day12.model.ProjectRole;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID memberId,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        ProjectRole role,
        Instant joinedAt
) {}
