package com.learning.springbootonboarding.day12.dto;

import com.learning.springbootonboarding.day12.model.TaskPriority;
import com.learning.springbootonboarding.day12.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record TaskRequest(
        @NotBlank(message = "Task title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant dueDate
) {
}
