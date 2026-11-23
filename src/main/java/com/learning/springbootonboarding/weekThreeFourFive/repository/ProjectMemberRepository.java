package com.learning.springbootonboarding.weekThreeFourFive.repository;

import com.learning.springbootonboarding.weekThreeFourFive.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectMember> findAllByUserId(UUID userId);
    List<ProjectMember> findAllByProjectId(UUID projectId);
}