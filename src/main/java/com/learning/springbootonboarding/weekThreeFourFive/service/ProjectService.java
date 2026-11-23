package com.learning.springbootonboarding.day12.service;

import com.learning.springbootonboarding.day12.dto.ProjectRequest;
import com.learning.springbootonboarding.day12.dto.ProjectResponse;
import com.learning.springbootonboarding.day12.model.Project;
import com.learning.springbootonboarding.day12.model.ProjectMember;
import com.learning.springbootonboarding.day12.model.ProjectRole;
import com.learning.springbootonboarding.day12.model.User;
import com.learning.springbootonboarding.day12.repository.ProjectMemberRepository;
import com.learning.springbootonboarding.day12.repository.ProjectRepository;
import com.learning.springbootonboarding.day12.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;
    private final ProjectMemberRepository projectMemberRepository;

//    RequiredArgsConstructor this will auto generate constructor at compile time
//    public ProjectService(ProjectRepository projectRepository) {
//        this.projectRepository = projectRepository;
//    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Project project = new Project(request.name().trim());
        project.setCreatedBy(currentUser);
        Project saved = projectRepository.save(project);

        ProjectMember owner = new ProjectMember(saved, currentUser, ProjectRole.OWNER);
        projectMemberRepository.save(owner);

        return toResponse(saved);
    }

    // Only return projects the current user is a member of
    public List<ProjectResponse> getAllProjects() {
        UUID currentUserId = currentUserService.getCurrentUserId();
        return projectMemberRepository.findAllByUserId(currentUserId)
                .stream()
                .map(member -> toResponse(member.getProject()))
                .toList();
    }

    // Only allow viewing if the user is a member of this project
    public ProjectResponse getProjectById(UUID id) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found or access denied: " + id));
        return toResponse(member.getProject());
    }

    // Only allow OWNER or ADMIN to update project details
    @Transactional
    public Project updateProject(UUID id, Project project) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));

        if (member.getRole() == ProjectRole.MEMBER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only OWNER or ADMIN can update this project");
        }

        Project existingProject = member.getProject();
        existingProject.setName(project.getName().trim());
        return projectRepository.save(existingProject);
    }


    // Only allow OWNER to delete the project
    @Transactional
    public void deleteProject(UUID id) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));

        if (member.getRole() != ProjectRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only project OWNER can delete this project");
        }

        projectRepository.delete(member.getProject());
    }


    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getCreatedAt()
        );
    }
}
