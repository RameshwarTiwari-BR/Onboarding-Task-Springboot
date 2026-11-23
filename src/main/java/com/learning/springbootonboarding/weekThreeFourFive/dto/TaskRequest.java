package com.learning.springbootonboarding.weekThreeFourFive.dto;

import com.learning.springbootonboarding.weekThreeFourFive.model.TaskPriority;
import com.learning.springbootonboarding.weekThreeFourFive.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public record TaskRequest(
        @NotBlank(message = "Task title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant dueDate,
        UUID assignedTo
) {
}
