package com.learning.springbootonboarding.day12.controller;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.day12.dto.TaskRequest;
import com.learning.springbootonboarding.day12.dto.TaskResponse;
import com.learning.springbootonboarding.day12.model.TaskStatus;
import com.learning.springbootonboarding.day12.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learning.springbootonboarding.day12.model.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @PostMapping(ApiEndPoints.PROJECT_V1 + "/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskRequest request
    ) {
        TaskResponse created = taskService.createTask(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(ApiEndPoints.PROJECT_V1 + "/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus status
    ) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, status));
    }

    @PutMapping(ApiEndPoints.TASK_V1 + "/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @DeleteMapping(ApiEndPoints.TASK_V1 + "/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(ApiEndPoints.TASK_V1)
    public ResponseEntity<Page<TaskResponse>> getAllTask(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) TaskStatus status) {

        return ResponseEntity.ok(taskService.getAllTask(page, size, sortBy, sortDir, status));
    }

}
