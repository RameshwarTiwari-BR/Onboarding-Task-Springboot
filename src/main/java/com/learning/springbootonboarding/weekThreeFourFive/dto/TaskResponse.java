package com.learning.springbootonboarding.weekThreeFourFive.dto;

import com.learning.springbootonboarding.weekThreeFourFive.model.TaskPriority;
import com.learning.springbootonboarding.weekThreeFourFive.model.TaskStatus;
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
        Instant dueDate,
        UUID assignedTo,
        UUID createdBy
) {
}
