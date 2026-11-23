package com.learning.springbootonboarding.weekThreeFourFive.dto;

import com.learning.springbootonboarding.weekThreeFourFive.model.ProjectRole;

import java.time.Instant;
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
