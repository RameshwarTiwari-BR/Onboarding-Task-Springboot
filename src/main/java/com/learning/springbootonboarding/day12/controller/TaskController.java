package com.learning.springbootonboarding.day12.controller;

import com.learning.springbootonboarding.day12.dto.CreateTaskRequest;
import com.learning.springbootonboarding.day12.dto.TaskResponse;
import com.learning.springbootonboarding.day12.dto.UpdateTaskStatusRequest;
import com.learning.springbootonboarding.day12.model.Task;
import com.learning.springbootonboarding.day12.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learning.springbootonboarding.constants.ApiEndPoints;

import java.util.List;

@RestController
@RequestMapping(ApiEndPoints.TASK_V1)
public class TaskController {

    private final TaskService taskService;

    // Constructor Injection (Automatically linked by Spring)
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // POST: Create a new Task
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(request.name());

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getName(),
                task.isStatus(),
                task.getTimestamp().toString()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
    }

    // GET: List all Tasks (Using Stream API mapping) so that we can convert into list and spring auto convert into json
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> responseList = taskService.getAllTasks().stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.isStatus(),
                        task.getTimestamp().toString()
                ))
                .toList();
        return ResponseEntity.ok(responseList);
    }

    // GET: Fetch a single task using an explicit Path Parameter
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.isStatus(),
                        task.getTimestamp().toString()
                ))
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found if missing
    }

    // PUT: Update an existing task's status by ID
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody UpdateTaskStatusRequest request) {

        return taskService.updateTaskStatus(id, request.status())
                .map(updatedTask -> new TaskResponse(
                        updatedTask.getId(),
                        updatedTask.getName(),
                        updatedTask.isStatus(),
                        updatedTask.getTimestamp().toString()
                ))
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK)) // 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found if ID doesn't exist
    }

//    Delete Task by Id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id)
            // If the task was found and deleted, return a 204 No Content success status
            .map(deletedTask -> new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
            // If the task didn't exist in the map, return a 404 Not Found error
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
