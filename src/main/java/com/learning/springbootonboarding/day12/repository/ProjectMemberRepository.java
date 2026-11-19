package com.learning.springbootonboarding.day12.repository;

import com.learning.springbootonboarding.day12.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
}
