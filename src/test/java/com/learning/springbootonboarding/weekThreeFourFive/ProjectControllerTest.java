
package com.learning.springbootonboarding.weekThreeFourFive;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.weekThreeFourFive.controller.ProjectController;
import com.learning.springbootonboarding.weekThreeFourFive.dto.ProjectRequest;
import com.learning.springbootonboarding.weekThreeFourFive.dto.ProjectResponse;
import com.learning.springbootonboarding.weekThreeFourFive.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @Test
    @DisplayName("POST - Returns 201 Created when valid")
    void createProject_Returns201() throws Exception {

        UUID projectId = UUID.randomUUID();

        ProjectRequest request = new ProjectRequest("New Project");

        ProjectResponse response = new ProjectResponse(
                projectId,
                "New Project",
                Instant.now()
        );

        when(projectService.createProject(any(ProjectRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(ApiEndPoints.PROJECT_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    @Test
    @DisplayName("POST - Returns 400 Bad Request when validation fails")
    void createProject_BlankName_Returns400() throws Exception {

        ProjectRequest invalidRequest = new ProjectRequest("");

        mockMvc.perform(post(ApiEndPoints.PROJECT_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("POST - Returns 409 Conflict on duplicate name")
    void createProject_Duplicate_Returns409() throws Exception {

        ProjectRequest request = new ProjectRequest("Duplicate");

        when(projectService.createProject(any(ProjectRequest.class)))
                .thenThrow(new DataIntegrityViolationException("uk_project_name"));

        mockMvc.perform(post(ApiEndPoints.PROJECT_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409));
    }

    @Test
    @DisplayName("DELETE /{id} - Returns 204 No Content")
    void deleteProject_Returns204() throws Exception {

        UUID projectId = UUID.randomUUID();

        mockMvc.perform(delete(ApiEndPoints.PROJECT_V1 + "/" + projectId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /{id} - Returns 404 Not Found when missing")
    void getProjectById_NotFound_Returns404() throws Exception {

        UUID projectId = UUID.randomUUID();

        when(projectService.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException("Project not found"));

        mockMvc.perform(get(ApiEndPoints.PROJECT_V1 + "/" + projectId))
                .andExpect(status().isNotFound());
    }
}
