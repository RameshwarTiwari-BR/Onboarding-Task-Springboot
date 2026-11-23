package com.learning.springbootonboarding.weekThreeFourFive.controller;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.weekThreeFourFive.dto.ProjectRequest;
import com.learning.springbootonboarding.weekThreeFourFive.dto.ProjectResponse;
import com.learning.springbootonboarding.weekThreeFourFive.model.Project;
import com.learning.springbootonboarding.weekThreeFourFive.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiEndPoints.PROJECT_V1)
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

//    We have lombok so RequiredArgsConstructor will auto generate this constructor at compile time..
//    public ProjectController(ProjectService projectService) {
//        this.projectService = projectService;
//    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse created = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProjectById(
            @PathVariable UUID id,
            @RequestBody Project request)
    {
        Project response = projectService.updateProject(id, request);
        return ResponseEntity.ok(response);

    }
}
