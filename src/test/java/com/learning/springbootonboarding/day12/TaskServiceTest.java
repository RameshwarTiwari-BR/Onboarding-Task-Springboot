package com.learning.springbootonboarding.day12;

import com.learning.springbootonboarding.day12.dto.TaskRequest;
import com.learning.springbootonboarding.day12.dto.TaskResponse;
import com.learning.springbootonboarding.day12.model.Project;
import com.learning.springbootonboarding.day12.model.Task;
import com.learning.springbootonboarding.day12.model.TaskPriority; // Change to String if priority is a String
import com.learning.springbootonboarding.day12.model.TaskStatus;
import com.learning.springbootonboarding.day12.repository.ProjectRepository;
import com.learning.springbootonboarding.day12.repository.TaskRepository;
import com.learning.springbootonboarding.day12.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("Create Task - Success when Project exists")
    void createTask_Success() {
        // Arrange
        Long projectId = 1L;
        Project project = new Project("Backend API");
        project.setId(projectId);

        TaskRequest request = new TaskRequest(
                "Write Unit Tests",
                "Cover service layer with Mockito",
                TaskStatus.PENDING,
                TaskPriority.HIGH,
                Instant.now().plus(3, ChronoUnit.DAYS)
        );

        Task savedTask = new Task(
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                project,
                request.dueDate()
        );
        savedTask.setId(10L);
        savedTask.setCreatedAt(Instant.now());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask(projectId, request);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Write Unit Tests", response.title());
        assertEquals(projectId, response.projectId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Create Task - Rejected when parent Project ID does not exist")
    void createTask_ProjectNotFound_ThrowsException() {
        // Arrange
        Long missingProjectId = 99L;
        TaskRequest request = new TaskRequest(
                "Write Unit Tests",
                "Description",
                TaskStatus.PENDING,
                TaskPriority.MEDIUM,
                Instant.now().plus(1, ChronoUnit.DAYS)
        );

        when(projectRepository.findById(missingProjectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.createTask(missingProjectId, request));
        verify(taskRepository, never()).save(any());
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Update Task - Success")
    void updateTask_Success() {
        // Arrange
        Long taskId = 10L;
        Project project = new Project("Backend API");
        project.setId(1L);

        Task existingTask = new Task(
                "Old Title",
                "Old Desc",
                TaskStatus.PENDING,
                TaskPriority.LOW,
                project,
                Instant.now().plus(1, ChronoUnit.DAYS)
        );
        existingTask.setId(taskId);

        TaskRequest updateRequest = new TaskRequest(
                "Updated Title",
                "Updated Desc",
                TaskStatus.COMPLETED,
                TaskPriority.HIGH,
                Instant.now().plus(5, ChronoUnit.DAYS)
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act
        TaskResponse response = taskService.updateTask(taskId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Title", response.title());
        assertEquals("Updated Desc", response.description());
        assertEquals(TaskStatus.COMPLETED, response.status());
        assertEquals(TaskPriority.HIGH, response.priority());
    }

    @Test
    @DisplayName("Update Task - Rejected when Task ID does not exist")
    void updateTask_NotFound_ThrowsException() {
        // Arrange
        Long missingTaskId = 99L;
        TaskRequest updateRequest = new TaskRequest(
                "Updated Title",
                "Updated Desc",
                TaskStatus.COMPLETED,
                TaskPriority.HIGH,
                Instant.now().plus(5, ChronoUnit.DAYS)
        );

        when(taskRepository.findById(missingTaskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(missingTaskId, updateRequest));
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Delete Task - Success")
    void deleteTask_Success() {
        // Arrange
        Long taskId = 10L;
        when(taskRepository.existsById(taskId)).thenReturn(true);

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("Delete Task - Rejected when Task ID does not exist")
    void deleteTask_NotFound_ThrowsException() {
        // Arrange
        Long missingTaskId = 99L;
        when(taskRepository.existsById(missingTaskId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(missingTaskId));
        verify(taskRepository, never()).deleteById(any());
    }
}
