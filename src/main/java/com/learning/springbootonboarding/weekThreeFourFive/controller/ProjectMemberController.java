package com.learning.springbootonboarding.day12.controller;

import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.day12.dto.AddProjectMemberRequest;
import com.learning.springbootonboarding.day12.dto.ProjectMemberResponse;
import com.learning.springbootonboarding.day12.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndPoints.PROJECT_V1+ "{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable UUID projectId,
            @Valid @RequestBody AddProjectMemberRequest request) {
        ProjectMemberResponse created = projectMemberService.addMember(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }
}
