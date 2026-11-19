package com.learning.springbootonboarding.day12.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDto(

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 255)
        String email,

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName

) {}