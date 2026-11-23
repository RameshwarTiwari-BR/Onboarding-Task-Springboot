package com.learning.springbootonboarding.day12.service;

import com.learning.springbootonboarding.day12.dto.AddProjectMemberRequest;
import com.learning.springbootonboarding.day12.dto.ProjectMemberResponse;
import com.learning.springbootonboarding.day12.model.ProjectMember;
import com.learning.springbootonboarding.day12.model.ProjectRole;
import com.learning.springbootonboarding.day12.model.User;
import com.learning.springbootonboarding.day12.repository.ProjectMemberRepository;
import com.learning.springbootonboarding.day12.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ProjectMemberResponse addMember(UUID projectId, AddProjectMemberRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();

        // 1. Verify caller is OWNER or ADMIN of this project
        ProjectMember callerMembership = projectMemberRepository
                .findByProjectIdAndUserId(projectId, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found or access denied"));

        if (callerMembership.getRole() == ProjectRole.MEMBER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only project OWNER or ADMIN can add members"
            );
        }

        // 2. Enforce single-OWNER rule (uq_project_single_owner)
        if (request.role() == ProjectRole.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot assign OWNER role; a project can only have one OWNER"
            );
        }

        // 3. Find the target user by email
        User targetUser = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No registered user found with email: " + request.email()
                ));

        // 4. Prevent duplicate membership (uq_project_member)
        if (projectMemberRepository.findByProjectIdAndUserId(projectId, targetUser.getId()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already a member of this project"
            );
        }

        // 5. Save membership
        ProjectMember newMember = new ProjectMember(
                callerMembership.getProject(),
                targetUser,
                request.role()
        );
        ProjectMember saved = projectMemberRepository.save(newMember);

        return toResponse(saved);
    }

    public List<ProjectMemberResponse> getProjectMembers(UUID projectId) {
        UUID currentUserId = currentUserService.getCurrentUserId();

        // Verify caller belongs to this project
        projectMemberRepository.findByProjectIdAndUserId(projectId, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found or access denied"));

        return projectMemberRepository.findAllByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProjectMemberResponse toResponse(ProjectMember member) {
        User u = member.getUser();
        return new ProjectMemberResponse(
                member.getId(),
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
