package com.learning.springbootonboarding.day12.dto;

import com.learning.springbootonboarding.day12.model.TaskPriority;
import com.learning.springbootonboarding.day12.model.TaskStatus;
import java.time.Instant;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long projectId,
        Instant createdAt,
        Instant dueDate
) {
}
