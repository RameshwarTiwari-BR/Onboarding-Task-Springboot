package com.learning.springbootonboarding.day12;

import com.learning.springbootonboarding.day12.dto.ProjectRequest;
import com.learning.springbootonboarding.day12.dto.ProjectResponse;
import com.learning.springbootonboarding.day12.model.Project;
import com.learning.springbootonboarding.day12.repository.ProjectRepository;
import com.learning.springbootonboarding.day12.repository.TaskRepository;
import com.learning.springbootonboarding.day12.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("Create Project - Success")
    void createProject_Success() {
        // Arrange
        ProjectRequest request = new ProjectRequest("Spring Boot Onboarding");
        Project savedProject = new Project("Spring Boot Onboarding");
        savedProject.setId(1L);
        savedProject.setCreatedAt(Instant.now());

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // Act
        ProjectResponse response = projectService.createProject(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Spring Boot Onboarding", response.name());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Create Project - Rejected when duplicate name violates DB constraint")
    void createProject_DuplicateName_ThrowsException() {
        // Arrange
        ProjectRequest request = new ProjectRequest("Existing Project");
        when(projectRepository.save(any(Project.class)))
                .thenThrow(new DataIntegrityViolationException("uk_project_name"));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> projectService.createProject(request));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Update Project - Success")
    void updateProject_Success() {
        // Arrange
        Long projectId = 1L;
        Project existingProject = new Project("Old Name");
        existingProject.setId(projectId);

        Project updatePayload = new Project("New Name");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(existingProject);

        // Act
        Project updated = projectService.updateProject(projectId, updatePayload);

        // Assert
        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
        verify(projectRepository, times(1)).save(existingProject);
    }

    @Test
    @DisplayName("Update Project - Rejected when ID does not exist")
    void updateProject_NotFound_ThrowsException() {
        // Arrange
        Long missingId = 99L;
        Project updatePayload = new Project("New Name");

        when(projectRepository.findById(missingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> projectService.updateProject(missingId, updatePayload));
        verify(projectRepository, never()).save(any());
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Delete Project - Success")
    void deleteProject_Success() {
        // Arrange
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(true);

        // Act
        projectService.deleteProject(projectId);

        // Assert
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    @DisplayName("Delete Project - Rejected when ID does not exist")
    void deleteProject_NotFound_ThrowsException() {
        // Arrange
        Long missingId = 99L;
        when(projectRepository.existsById(missingId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> projectService.deleteProject(missingId));
        verify(projectRepository, never()).deleteById(any());
    }
}
