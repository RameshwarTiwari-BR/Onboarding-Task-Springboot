
package com.learning.springbootonboarding.weekThreeFourFive;

import com.learning.springbootonboarding.weekThreeFourFive.dto.TaskRequest;
import com.learning.springbootonboarding.weekThreeFourFive.dto.TaskResponse;
import com.learning.springbootonboarding.weekThreeFourFive.model.*;
import com.learning.springbootonboarding.weekThreeFourFive.repository.ProjectRepository;
import com.learning.springbootonboarding.weekThreeFourFive.repository.TaskRepository;
import com.learning.springbootonboarding.weekThreeFourFive.service.CurrentUserService;
import com.learning.springbootonboarding.weekThreeFourFive.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TaskService taskService;


    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("Create Task - Success when Project exists")
    void createTask_Success() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User mockUser = new User("dev@example.com", "Dev", "User");
        mockUser.setId(userId);

        Project project = new Project("Backend API");
        project.setId(projectId);

        TaskRequest request = new TaskRequest(
                "Write Unit Tests",
                "Cover service layer with Mockito",
                TaskStatus.PENDING,
                TaskPriority.HIGH,
                Instant.now().plus(3, ChronoUnit.DAYS),
                userId
        );

        Task savedTask = new Task(
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                project,
                request.dueDate()
        );
        savedTask.setId(taskId);
        savedTask.setCreatedAt(Instant.now());

        when(currentUserService.getCurrentUser()).thenReturn(mockUser);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask(projectId, request);

        // Assert
        assertNotNull(response);
        assertEquals(taskId, response.id());
        assertEquals("Write Unit Tests", response.title());
        assertEquals(projectId, response.projectId());

        verify(taskRepository, times(1)).save(any(Task.class));
    }


    @Test
    @DisplayName("Create Task - Rejected when parent Project ID does not exist")
    void createTask_ProjectNotFound_ThrowsException() {
        // Arrange
        UUID missingProjectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User mockUser = new User("dev@example.com", "Dev", "User");
        mockUser.setId(userId);

        TaskRequest request = new TaskRequest(
                "Write Unit Tests",
                "Description",
                TaskStatus.PENDING,
                TaskPriority.MEDIUM,
                Instant.now().plus(1, ChronoUnit.DAYS),
                UUID.randomUUID()
        );

        when(currentUserService.getCurrentUser()).thenReturn(mockUser);
        when(projectRepository.findById(missingProjectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> taskService.createTask(missingProjectId, request)
        );

        verify(taskRepository, never()).save(any());
    }


    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Update Task - Success")
    void updateTask_Success() {

        // Arrange
        UUID taskId = UUID.randomUUID();

        Project project = new Project("Backend API");
        project.setId(UUID.randomUUID());

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
                Instant.now().plus(5, ChronoUnit.DAYS),
                UUID.randomUUID()
        );

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(existingTask));

//        when(taskRepository.save(existingTask))
//                .thenReturn(existingTask);

        // Act
        TaskResponse response = taskService.updateTask(taskId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Title", response.title());
        assertEquals("Updated Desc", response.description());
        assertEquals(TaskStatus.COMPLETED, response.status());
        assertEquals(TaskPriority.HIGH, response.priority());

//        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    @DisplayName("Update Task - Rejected when Task ID does not exist")
    void updateTask_NotFound_ThrowsException() {

        // Arrange
        UUID missingTaskId = UUID.randomUUID();

        TaskRequest updateRequest = new TaskRequest(
                "Updated Title",
                "Updated Desc",
                TaskStatus.COMPLETED,
                TaskPriority.HIGH,
                Instant.now().plus(5, ChronoUnit.DAYS),
                missingTaskId
        );

        when(taskRepository.findById(missingTaskId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> taskService.updateTask(missingTaskId, updateRequest)
        );

        verify(taskRepository, never()).save(any());
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Delete Task - Success")
    void deleteTask_Success() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // 1. Create and mock the current user
        User mockUser = new User();
        mockUser.setId(userId);
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        // 2. Create and mock the task owned by that user
        Task mockTask = new Task();
        mockTask.setId(taskId);
        mockTask.setCreatedBy(userId); // Pass mockUser instead if createdBy is a User object

        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskRepository, times(1)).deleteById(taskId);
    }


    @Test
    @DisplayName("Delete Task - Rejected when Task ID does not exist")
    void deleteTask_NotFound_ThrowsException() {

        // Arrange
        UUID missingTaskId = UUID.randomUUID();

        when(taskRepository.existsById(missingTaskId))
                .thenReturn(false);

        // Act & Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> taskService.deleteTask(missingTaskId)
        );

        verify(taskRepository, never()).deleteById(any());
    }
}
