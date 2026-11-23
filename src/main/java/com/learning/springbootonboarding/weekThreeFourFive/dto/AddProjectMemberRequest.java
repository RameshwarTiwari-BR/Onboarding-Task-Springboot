package com.learning.springbootonboarding.day12.dto;

import com.learning.springbootonboarding.day12.model.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AddProjectMemberRequest(
        @NotBlank(message = "User email is required")
        @Email(message = "Enter a valid email address")
        String email,

        @NotNull(message = "Role is required (ADMIN or MEMBER)")
        ProjectRole role
) {}
