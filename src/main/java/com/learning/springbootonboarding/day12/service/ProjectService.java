package com.learning.springbootonboarding.day12.service;

import com.learning.springbootonboarding.day12.dto.ProjectRequest;
import com.learning.springbootonboarding.day12.dto.ProjectResponse;
import com.learning.springbootonboarding.day12.model.Project;
import com.learning.springbootonboarding.day12.repository.ProjectRepository;
import com.learning.springbootonboarding.day12.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

//    RequiredArgsConstructor this will auto generate constructor at compile time
//    public ProjectService(ProjectRepository projectRepository) {
//        this.projectRepository = projectRepository;
//    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project(request.name());
        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getProjectById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        return toResponse(project);
    }

    @Transactional
    public void deleteProject(UUID id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public Project updateProject(UUID id, Project project){
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project Id not found" + id));
        existingProject.setName(project.getName());

        return projectRepository.save(existingProject);

    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getCreatedAt()
        );
    }
}
