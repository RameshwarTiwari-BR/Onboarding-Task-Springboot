package com.learning.springbootonboarding.day12.dto;

import com.learning.springbootonboarding.day12.model.TaskPriority;
import com.learning.springbootonboarding.day12.model.TaskStatus;
import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        UUID projectId,
        Instant createdAt,
        Instant dueDate
) {
}
