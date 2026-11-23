package com.learning.springbootonboarding.weekThreeFourFive.repository;

import com.learning.springbootonboarding.weekThreeFourFive.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> id(UUID id);
}
