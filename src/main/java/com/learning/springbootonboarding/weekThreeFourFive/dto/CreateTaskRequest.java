package com.learning.springbootonboarding.day12.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// What the frontend must send in the JSON body
public record CreateTaskRequest(
        @NotBlank(message = "Task Name is required")
        @Size(min=10, max= 200, message = "Task name must be between 10 to 200 characters")
        String name
) {}

