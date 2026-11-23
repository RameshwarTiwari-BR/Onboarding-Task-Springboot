package com.learning.springbootonboarding.weekThreeFourFive.dto;

import com.learning.springbootonboarding.weekThreeFourFive.model.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddProjectMemberRequest(
        @NotBlank(message = "User email is required")
        @Email(message = "Enter a valid email address")
        String email,

        @NotNull(message = "Role is required (ADMIN or MEMBER)")
        ProjectRole role
) {}
